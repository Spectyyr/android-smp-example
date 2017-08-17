package com.sessionm.smp_receipt;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.receipt.ReceiptsManager;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String SAMPLE_USER_TOKEN = "v2--Sd2T8UBqlCGQovVPnsUs4eqwFe0-1i9JV4nq__RWmsA=--dWM8r8RggUJCToOaiiT6NXmiOipkovvD9HueM_jZECStExtGFkZzVmCUhkdDJe5NQw==";

    private static final int WRITE_EXTERNAL_PERMISSION_REQUEST_CODE = 1;
    private TextView userBalanceTextView;
    private FloatingActionButton newUploadButton;

    private SessionM sessionM = SessionM.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        newUploadButton = (FloatingActionButton) findViewById(R.id.action_upload_receipt);
        newUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission to access the location is missing.
                    PermissionUtils.requestPermission(MainActivity.this, WRITE_EXTERNAL_PERMISSION_REQUEST_CODE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                } else {
                    checkHasIncompleteReceipts();
                }
            }
        });

        userBalanceTextView = (TextView) findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserManager.getInstance().getCurrentUser() == null)
                    IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
                else
                    IdentityManager.getInstance().logOutUser();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserManager.getInstance().setListener(_userListener);
    }

    UserListener _userListener = new UserListener() {
        @Override
        public void onUserUpdated(SMPUser smpUser, Set<String> set) {
            if (smpUser != null) {
                userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
            } else
                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
            ReceiptsManager.getInstance().fetchReceipts(100, 1);
        }

        @Override
        public void onFailure(SessionMError sessionMError) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SessionM.RECEIPT_UPLOAD_RESULT_CODE) {
            if (resultCode == RESULT_OK)
                Toast.makeText(this, "Receipt uploaded!", Toast.LENGTH_SHORT).show();
            else if (resultCode == RESULT_CANCELED) {
                String errorMessage = "Back button";
                if (intent != null)
                    errorMessage = intent.getStringExtra("result");
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkHasIncompleteReceipts() {
        ReceiptsManager receiptsManager = SessionM.getInstance().getReceiptsManager();
        if (receiptsManager.hasIncompleteReceipts()) {
            popUpUploadIncompleteReceiptsDialog();
        } else {
            startUploadReceipt();
        }
    }

    private void startUploadReceipt() {
        sessionM.getReceiptsManager().setUploadReceiptActivityColors(null, null, null, "#A3BE5F", null);
        sessionM.getReceiptsManager().startUploadReceiptActivity(this, "14821", null, null);
        Intent startIntent = new Intent(MainActivity.this, ReceiptUploadingService.class);
        startService(startIntent);
    }

    protected void popUpUploadIncompleteReceiptsDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_upload_incomplete_receipts, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sessionM.getReceiptsManager().uploadIncompleteReceipt(null, false);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Incomplete receipt uploading canceled!", Toast.LENGTH_SHORT).show();
                startUploadReceipt();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setView(dialogLayout);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != WRITE_EXTERNAL_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Enable the my location layer if the permission has been granted.
            checkHasIncompleteReceipts();
        } else {
            // Display the missing permission error dialog when the fragments resume.
        }
    }
}
