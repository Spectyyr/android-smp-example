package com.sessionm.smp_auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityListener;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.data.AuthCredential;
import com.sessionm.api.identity.data.SMPUser;

/**
 * Demonstrate SMP Authentication using a Facebook ID Token.
 */
public class FacebookLoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SessionM.GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private IdentityManager identityManager;

    private IdentityListener identityListener;

    private LoginButton loginButton;
    private TextView mStatusTextView;
    private TextView mDetailTextView;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);

        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                authWithFacebook(loginResult);
            }

            @Override
            public void onCancel() {
                Toast.makeText(FacebookLoginActivity.this, "Facebook login canceled.", Toast.LENGTH_SHORT).show();
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(FacebookLoginActivity.this, "Facebook login error.", Toast.LENGTH_SHORT).show();
                // App code
            }
        });

        identityManager = IdentityManager.getInstance();

        identityListener = new IdentityListener() {
            @Override
            public void onUserStateUpdated(SMPUser smpUser) {
                hideProgressDialog();
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
                hideProgressDialog();
                Toast.makeText(FacebookLoginActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        identityManager.setIdentityListener(identityListener);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void authWithFacebook(LoginResult loginResult) {
        Log.d(TAG, "authWithFacebook:" + loginResult.getAccessToken().getToken());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = new AuthCredential(AuthCredential.Provider.FACEBOOK, loginResult.getAccessToken().getToken());
        identityManager.authenticateWithCredential(credential);
    }

    private void signOut() {
        identityManager.logOutUser();
        LoginManager.getInstance().logOut();
        updateUI(null);
    }

    private void updateUI(SMPUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.smp_status_fmt, user.getID()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.logged_out);
            mDetailTextView.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_out_button) {
            signOut();
        }
    }
}
