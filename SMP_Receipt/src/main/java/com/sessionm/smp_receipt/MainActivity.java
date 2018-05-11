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

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.provider.SessionMOauthProvider;
import com.sessionm.receipt.api.ReceiptsManager;
import com.sessionm.smp_receipt.upload.ReceiptUploadActivity;
import com.sessionm.smp_receipt.upload.ReceiptUploadingService;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_PERMISSION_REQUEST_CODE = 1;
    private static final String TEST_RECEIPT_CAMPAIGN_ID = "138";
    private FloatingActionButton _newUploadButton;

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";
    private static final String SAMPLE_USER_EMAIL = "sampleuser@sessionm.com";
    private static final String SAMPLE_USER_PWD = "sessionm1";
    private TextView userBalanceTextView;
    private SessionMOauthProvider _sessionMOauthProvider;
    private UserManager _userManager;
    private FloatingActionButton referAFriendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_activity);
        Toolbar actionBar = findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);
        _newUploadButton = findViewById(R.id.action_upload_receipt);
        _newUploadButton.setOnClickListener(new View.OnClickListener() {
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

        _sessionMOauthProvider = (SessionMOauthProvider) SessionM.getAuthenticationProvider();
        _userManager = UserManager.getInstance();

        userBalanceTextView = findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserManager.getInstance().getCurrentUser() == null) {
                    _sessionMOauthProvider.authenticateUser(SAMPLE_USER_EMAIL, SAMPLE_USER_PWD, new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (sessionMError != null) {
                                Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                fetchUser();
                            }
                        }
                    });
                }
                else {
                    _sessionMOauthProvider.logoutUser(new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (authenticatedState.equals(SessionMOauthProvider.AuthenticatedState.NotAuthenticated)) {
                                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
                                refreshUI();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
        if (_sessionMOauthProvider.isAuthenticated())
            fetchUser();
    }

    private void fetchUser() {
        _userManager.fetchUser(new UserManager.OnUserFetchedListener() {
            @Override
            public void onFetched(SMPUser smpUser, Set<String> set, SessionMError sessionMError) {
                if (sessionMError != null) {
                    Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (smpUser != null) {
                        userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
                    } else
                        userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
                }
                refreshUI();
            }
        });
    }

    private void refreshUI() {
        ReceiptsFragment f = (ReceiptsFragment) getSupportFragmentManager().findFragmentById(R.id.receipts_fragment);
        f.onRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1) {
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
        ReceiptsManager receiptsManager = ReceiptsManager.getInstance();
        if (receiptsManager.hasIncompleteReceipts()) {
            popUpUploadIncompleteReceiptsDialog();
        } else {
            startUploadReceipt();
        }
    }

    private void startUploadReceipt() {
        Intent startIntent = new Intent(this, ReceiptUploadingService.class);
        startService(startIntent);
        Bundle bundle = new Bundle();
        bundle.putBoolean("welcome", true);
        bundle.putString("campaign_id", TEST_RECEIPT_CAMPAIGN_ID);
        Intent uploadIntent = new Intent(this, ReceiptUploadActivity.class);
        uploadIntent.putExtras(bundle);
        this.startActivity(uploadIntent);
    }

    protected void popUpUploadIncompleteReceiptsDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.receipt_upload_incomplete_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ReceiptsManager.getInstance().uploadIncompleteReceipt(null, false);
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
