package com.sessionm.smp_auth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityListener;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.data.SMPUser;

/**
 * Run following command to trigger custom authentication login action:
 *
 * adb shell am broadcast -a com.google.example.ACTION_TOKEN --es key_token v2--Sd2T8UBqlCGQovVPnsUs4eqwFe0-1i9JV4nq__RWmsA=--dWM8r8RggUJCToOaiiT6NXmiOipkovvD9HueM_jZECStExtGFkZzVmCUhkdDJe5NQw==
 *
 * This key_token is for sample SMP user. It can be replaced by any custom provider/token.
 */
public class CustomAuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CustomAuthActivity";

    private IdentityManager identityManager;
    private IdentityListener identityListener;

    private String mCustomToken;
    private TokenBroadcastReceiver mTokenReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        // Button click listeners
        findViewById(R.id.button_sign_in).setOnClickListener(this);

        mTokenReceiver = new TokenBroadcastReceiver() {
            @Override
            public void onNewToken(String token) {
                Log.d(TAG, "onNewToken:" + token);
                setCustomToken(token);
            }
        };

        identityManager = IdentityManager.getInstance();

        identityListener = new IdentityListener() {
            @Override
            public void onUserStateUpdated(SMPUser smpUser) {
                if (smpUser != null) {
                    // User is signed in
                    Toast.makeText(CustomAuthActivity.this, "Logged in.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onUserStateUpdated: user " + smpUser.getID());
                } else {
                    Toast.makeText(CustomAuthActivity.this, "Logged out.", Toast.LENGTH_SHORT).show();
                    // User is signed out
                    Log.d(TAG, "onUserStateUpdated:signed_out");
                }
                updateUI(smpUser);
            }

            @Override
            public void onFailure(SessionMError sessionMError) {
                Toast.makeText(CustomAuthActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        identityManager.setIdentityListener(identityListener);
        registerReceiver(mTokenReceiver, TokenBroadcastReceiver.getFilter());
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mTokenReceiver);
    }

    private void startSignIn() {
        identityManager.authenticateWithCustomToken(mCustomToken, "custom_provider");
    }

    private void updateUI(SMPUser user) {
        if (user != null) {
            ((TextView) findViewById(R.id.text_sign_in_status)).setText(
                    "User ID: " + user.getID());
        } else {
            ((TextView) findViewById(R.id.text_sign_in_status)).setText(
                    "Error: log in failed.");
        }
    }

    private void setCustomToken(String token) {
        mCustomToken = token;

        String status;
        if (mCustomToken != null) {
            status = "Token:" + mCustomToken;
        } else {
            status = "Token: null";
        }

        // Enable/disable sign-in button and show the token
        findViewById(R.id.button_sign_in).setEnabled((mCustomToken != null));
        ((TextView) findViewById(R.id.text_token_status)).setText(status);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_in) {
            startSignIn();
        }
    }
}
