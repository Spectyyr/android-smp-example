package com.sessionm.smp_auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sessionm.core.api.SessionMError;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.data.SMPUserUpdate;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class UserDetailsActivity extends AppCompatActivity {

    TableLayout _userTable;
    LinearLayout _updateLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        _userTable = findViewById(R.id.mmc_user_table);

        Button updateButton = findViewById(R.id.mmc_user_update_btn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpUpdateUserDialog();
            }
        });

        findViewById(R.id.present_user_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionMError sessionMError = UserManager.getInstance().getWebProfileManager().updateUserProfile(UserDetailsActivity.this);
                if (sessionMError != null)
                    Utils.createAlertDialog(UserDetailsActivity.this, sessionMError);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserManager.getInstance().fetchUser(new UserManager.OnUserFetchedListener() {
            @Override
            public void onFetched(SMPUser user, Set<String> deltas, SessionMError error) {
                handleUserCallback(user, deltas, error);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void refreshUI(SMPUser user) {
        Map<String, Object> userMap = Utils.getAllGettersValue("com.sessionm.identity.api.data.SMPUser", user);
        if (userMap != null) {
            _userTable.removeAllViews();
            for (String key : userMap.keySet()) {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                TextView textView = new TextView(this);
                textView.setText(key + ": " + userMap.get(key));
                row.addView(textView);
                _userTable.addView(row);
            }
        }
    }

    private void popUpUpdateUserDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_update_user, null);
        _updateLayout = dialogLayout.findViewById(R.id.update_user_layout);

        Map<String, Object> userMap = Utils.getAllGettersValue("com.sessionm.identity.api.data.SMPUser", UserManager.getInstance().getCurrentUser());
        if (userMap != null) {
            userMap.put("IPAddress", "");
            for (String key : userMap.keySet()) {
                EditText row = new EditText(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setTag(key);
                row.setHint(key + ": " + userMap.get(key));
                _updateLayout.addView(row);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SMPUserUpdate.Builder requestBuilder = new SMPUserUpdate.Builder()
                        .externalID(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("ExternalID")))
                        .email(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("Email")))
                        .gender(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("Gender")))
                        .firstName(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("FirstName")))
                        .lastName(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("LastName")))
                        .dateOfBirth(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("DateOfBirth")))
                        .DMA(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("DMA")))
                        .city(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("City")))
                        .state(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("State")))
                        .zipCode(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("ZipCode")))
                        .country(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("Country")))
                        .latitude(updateNonEmptyFieldDouble((EditText) _updateLayout.findViewWithTag("Latitude")))
                        .longitude(updateNonEmptyFieldDouble((EditText) _updateLayout.findViewWithTag("Longitude")))
                        .ipAddress(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("IPAddress")))
                        .locale(nullableLocale());
                UserManager.getInstance().updateUser(requestBuilder.build(), new UserManager.OnUserUpdatedListener() {
                    @Override
                    public void onUpdated(SMPUser user, Set<String> deltas, SessionMError error) {
                        handleUserCallback(user, deltas, error);
                    }
                });
            }
        });

        AlertDialog dialog = builder.create();


        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        dialog.show();
    }

    private void handleUserCallback(SMPUser user, Set<String> deltas, SessionMError error) {
        if (error != null)
            Utils.createAlertDialog(this, error);
        else {
            refreshUI(user);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }

    public static String updateNonEmptyField(EditText editText) {
        if (!editText.getText().toString().isEmpty())
            return editText.getText().toString();
        return null;
    }

    public static Double updateNonEmptyFieldDouble(EditText editText) {
        if (!editText.getText().toString().isEmpty())
            return Double.parseDouble(editText.getText().toString());
        return null;
    }

    private Locale nullableLocale() {
        try {
            return new Locale(updateNonEmptyField((EditText) _updateLayout.findViewWithTag("Locale")));
        } catch (NullPointerException e) {
            return null;
        }
    }
}
