package com.sessionm.smp_auth.email;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.data.SMPUserCreate;
import com.sessionm.identity.api.provider.SessionMOauthProvider;
import com.sessionm.smp_auth.BaseActivity;
import com.sessionm.smp_auth.R;
import com.sessionm.smp_auth.UserDetailsActivity;

import java.util.Set;

/**
 * Demonstrates the usage of the SMP SDK to create/authorize a user with email and password.
 */
public class EmailPasswordActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";
    //Sample user to test authentication, if needed
    private static final String SAMPLE_USER_EMAIL = "sampleuser@sessionm.com";
    private static final String SAMPLE_USER_PWD = "sessionm1";

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private TextView mAuthCodeTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    private SessionMOauthProvider _sessionMOauthProvider;
    private UserManager _userManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mAuthCodeTextView = findViewById(R.id.auth_code);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.logged_in_auth_code).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        findViewById(R.id.logged_in_view_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailPasswordActivity.this, UserDetailsActivity.class));
            }
        });

        _sessionMOauthProvider = (SessionMOauthProvider) SessionM.getAuthenticationProvider();
        _userManager = UserManager.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchUser();
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        SMPUserCreate.Builder builder = new SMPUserCreate.Builder(email, password).lastName("LastName");
        SessionMError error = _sessionMOauthProvider.createUser(builder.build(), new SessionMOauthProvider.SessionMOauthProviderListener() {
            @Override
            public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                hideProgressDialog();
                if (sessionMError != null) {
                    Toast.makeText(EmailPasswordActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                }
                fetchUser();
            }
        });
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

        SessionMError error = _sessionMOauthProvider.authenticateUser(email, password, new SessionMOauthProvider.SessionMOauthProviderListener() {
            @Override
            public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                hideProgressDialog();
                if (sessionMError != null) {
                    Toast.makeText(EmailPasswordActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                }
                fetchUser();
            }
        });
        if (error != null) {
            hideProgressDialog();
            Toast.makeText(EmailPasswordActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUser() {
        if (_sessionMOauthProvider.isAuthenticated()) {
            _userManager.fetchUser(new UserManager.OnUserFetchedListener() {
                @Override
                public void onFetched(SMPUser smpUser, Set<String> set, SessionMError sessionMError) {
                    if (sessionMError != null) {
                        Toast.makeText(EmailPasswordActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {

                        if (smpUser != null) {
                            // User is signed in
                            Log.d(TAG, "onAuthStateChanged:signed_in:" + smpUser.getID());
                        } else {
                            // User is signed out
                            Log.d(TAG, "onAuthStateChanged:signed_out");
                        }
                        updateUI(smpUser);
                    }
                }
            });
        }
    }

    private void requestAuthCode() {
        SessionMError error = _sessionMOauthProvider.getAuthCode(null, new SessionMOauthProvider.AuthCodeCallback() {
            @Override
            public void onAuthCodeRequested(String s, SessionMError sessionMError) {
                if (sessionMError != null)
                    Toast.makeText(EmailPasswordActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                else
                    mAuthCodeTextView.setText(s);
            }
        });
        if (error != null)
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
    }

    private void signOut() {
        _sessionMOauthProvider.logoutUser(null);
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
            mStatusTextView.setText(getString(R.string.smp_user_email_fmt, smpUser.getEmail()));
            mDetailTextView.setText(getString(R.string.smp_user_id_fmt, smpUser.getID()));
            mAuthCodeTextView.setText("Auth Code");

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            findViewById(R.id.logged_in_view_profile).setVisibility(View.VISIBLE);
            findViewById(R.id.logged_in_auth_code).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.logged_out);
            mDetailTextView.setText(null);
            mAuthCodeTextView.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.logged_in_view_profile).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.logged_in_auth_code) {
            requestAuthCode();
        } else if (i == R.id.sign_out_button) {
            signOut();
        }
    }
}
