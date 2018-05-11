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
import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.provider.SessionMOauthProvider;
import com.sessionm.smp_auth.BaseActivity;
import com.sessionm.smp_auth.R;
import com.sessionm.smp_auth.UserDetailsActivity;

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

    private SessionMOauthProvider _sessionMOauthProvider;
    private UserManager _userManager;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);

        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.logged_in_view_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FacebookLoginActivity.this, UserDetailsActivity.class));
            }
        });
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
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

        _sessionMOauthProvider = (SessionMOauthProvider) SessionM.getAuthenticationProvider();
        _userManager = UserManager.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchUser();
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
        _sessionMOauthProvider.authenticateUserWithToken(fbToken, "facebook", new SessionMOauthProvider.SessionMOauthProviderListener() {
            @Override
            public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                hideProgressDialog();
                if (sessionMError != null)
                    Toast.makeText(FacebookLoginActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                else {
                    fetchUser();
                }
            }
        });
        showProgressDialog();
        // [END_EXCLUDE]
    }

    private void fetchUser() {
        if (_sessionMOauthProvider.isAuthenticated()) {
            _userManager.fetchUser(new UserManager.OnUserFetchedListener() {
                @Override
                public void onFetched(SMPUser smpUser, Set<String> set, SessionMError sessionMError) {
                    if (sessionMError != null) {
                        Toast.makeText(FacebookLoginActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void signOut() {
        _sessionMOauthProvider.logoutUser(null);
        LoginManager.getInstance().logOut();
        updateUI(null);
    }

    private void updateUI(SMPUser smpUser) {
        hideProgressDialog();
        if (smpUser != null) {
            mStatusTextView.setText(getString(R.string.smp_user_email_fmt, smpUser.getEmail()));
            mDetailTextView.setText(getString(R.string.smp_user_id_fmt, smpUser.getID()));

            findViewById(R.id.login_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            findViewById(R.id.logged_in_view_profile).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.logged_out);
            mDetailTextView.setText(null);

            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.logged_in_view_profile).setVisibility(View.GONE);
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
