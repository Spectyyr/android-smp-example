package com.sessionm.smp_auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityListener;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.identity.data.SMPUserCreate;
import com.sessionm.api.identity.profile.UserProfileListener;
import com.sessionm.api.identity.profile.UserProfileManager;

public class EmailPasswordActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    private IdentityManager identityManager;
    private IdentityListener identityListener;
    private UserProfileManager userProfileManager;
    private UserProfileListener userProfileListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        mDetailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailPasswordActivity.this, UserDetailsActivity.class));
            }
        });

        identityManager = SessionM.getInstance().getIdentityManager();
        userProfileManager = identityManager.getUserProfileManager();

        identityListener = new IdentityListener() {
            @Override
            public void onAuthStateUpdated(IdentityManager.AuthState authState) {
                hideProgressDialog();
            }

            @Override
            public void onFailure(SessionMError sessionMError) {
                hideProgressDialog();
                Toast.makeText(EmailPasswordActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        userProfileListener = new UserProfileListener() {
            @Override
            public void onUserUpdated(SMPUser smpUser) {
                if (smpUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + smpUser.getID());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(smpUser);
            }

            @Override
            public void onFailure(SessionMError sessionMError) {
                Toast.makeText(EmailPasswordActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        identityManager.setListener(identityListener);
        userProfileManager.setListener(userProfileListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        identityManager.setListener(null);
        userProfileManager.setListener(null);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm()) {
//            return;
//        }

        showProgressDialog();

//        SMPUserCreate.Builder builder = new SMPUserCreate.Builder(email, password).lastName("LastName");
        SMPUserCreate.Builder builder = new SMPUserCreate.Builder(System.currentTimeMillis() + "@sessionm.com", "aaaaaaaa1").lastName("LastName");
        SessionMError error = identityManager.createUser(builder.build());
        if (error != null) {
            hideProgressDialog();
            Toast.makeText(EmailPasswordActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        SessionMError error = identityManager.authenticateUser(email, password);
        if (error != null) {
            hideProgressDialog();
            Toast.makeText(EmailPasswordActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void signOut() {
        identityManager.logOutUser();
        updateUI(null);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(SMPUser smpUser) {
        hideProgressDialog();
        if (smpUser != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt, smpUser.getEmail()));
            mDetailTextView.setText(getString(R.string.smp_status_fmt, smpUser.getID()));

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.logged_out);
            mDetailTextView.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        }
    }
}
