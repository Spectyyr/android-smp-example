package com.sessionm.smp_auth.facebook;

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
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.smp_auth.BaseActivity;
import com.sessionm.smp_auth.R;

import java.util.Set;

/**
 * Demonstrate SMP Authentication using a Facebook ID Token.
 */
public class FacebookLoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SessionM.FBActivity";
    private static final int RC_SIGN_IN = 9001;

    private LoginButton loginButton;
    private TextView mStatusTextView;
    private TextView mDetailTextView;

    private IdentityManager identityManager;
    private IdentityListener identityListener;
    private UserManager userManager;
    private UserListener userListener;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);

        findViewById(R.id.sign_out_button).setOnClickListener(this);

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
        userManager = UserManager.getInstance();

        identityListener = new IdentityListener() {
            @Override
            public void onAuthStateUpdated(IdentityManager.AuthState authState) {
                hideProgressDialog();
            }

            @Override
            public void onFailure(SessionMError sessionMError) {
                hideProgressDialog();
                Toast.makeText(FacebookLoginActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        userListener = new UserListener() {
            @Override
            public void onUserUpdated(SMPUser smpUser, Set<String> set) {
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
                Toast.makeText(FacebookLoginActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        identityManager.setListener(identityListener);
        userManager.setListener(userListener);
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
        String fbToken = loginResult.getAccessToken().getToken();
        Log.d(TAG, "authWithFacebook:" + fbToken);
        // [START_EXCLUDE silent]
        identityManager.authenticateWithToken(fbToken, "facebook");
        showProgressDialog();
        // [END_EXCLUDE]
    }

    private void signOut() {
        identityManager.logOutUser();
        LoginManager.getInstance().logOut();
        updateUI(null);
    }

    private void updateUI(SMPUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.facebook_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.smp_status_fmt, user.getID()));

            findViewById(R.id.login_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.logged_out);
            mDetailTextView.setText(null);

            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
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
