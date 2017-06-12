package com.sessionm.smp_auth.webauth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityListener;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.identity.webauth.WebAuthListener;
import com.sessionm.api.identity.webauth.WebAuthResponse;
import com.sessionm.smp_auth.BaseActivity;
import com.sessionm.smp_auth.R;
import com.sessionm.smp_auth.UserDetailsActivity;

import java.util.Set;

/**
 * Demonstrates the usage of the SMP SDK to authenticate a user with SessionM web authentication flow.
 */
public class WebAuthActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "AuthenticateToken";

    private TextView mStatusTextView;
    private TextView mDetailTextView;

    private IdentityManager identityManager;
    private UserManager userManager;
    private WebAuthListener webAuthListener;
    private UserListener userListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessionm_web_auth);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);

        // Buttons
        findViewById(R.id.authenticate_with_custom_tab).setOnClickListener(this);
        findViewById(R.id.authenticate_with_browser).setOnClickListener(this);
        findViewById(R.id.logged_in_view_profile).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        identityManager = IdentityManager.getInstance();
        userManager = UserManager.getInstance();

        webAuthListener = new WebAuthListener() {
            @Override
            public void onWebAuthFinished(WebAuthResponse webAuthResponse) {
                hideProgressDialog();
            }

            @Override
            public void onAuthStateUpdated(IdentityManager.AuthState authState) {
                hideProgressDialog();
            }

            @Override
            public void onFailure(SessionMError sessionMError) {
                hideProgressDialog();
                Toast.makeText(WebAuthActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(WebAuthActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        identityManager.setListener(webAuthListener);
        userManager.setListener(userListener);
    }

    private void authenticateWithWeb() {
        showProgressDialog();
        IdentityManager.getInstance().startWebAuthorization(this, IdentityManager.WebAuthResponseType.AccessToken);
    }

    private void signOut() {
        identityManager.logOutUser();
        updateUI(null);
    }

    private void updateUI(SMPUser smpUser) {
        hideProgressDialog();
        if (smpUser != null) {
            mStatusTextView.setText(getString(R.string.token_status_fmt, smpUser.getEmail()));
            mDetailTextView.setText(getString(R.string.smp_status_fmt, smpUser.getID()));

            findViewById(R.id.authenticate_with_custom_tab).setVisibility(View.GONE);
            findViewById(R.id.authenticate_with_browser).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            findViewById(R.id.logged_in_view_profile).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.logged_out);
            mDetailTextView.setText(null);

            findViewById(R.id.authenticate_with_custom_tab).setVisibility(View.VISIBLE);
            findViewById(R.id.authenticate_with_browser).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.logged_in_view_profile).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.authenticate_with_custom_tab)
            authenticateWithWeb();
        else if (i == R.id.authenticate_with_browser)
            authenticateWithWeb();
        else if (i == R.id.logged_in_view_profile)
            startActivity(new Intent(WebAuthActivity.this, UserDetailsActivity.class));
        else if (i == R.id.sign_out_button)
            signOut();
    }
}
