package com.sessionm.smp_auth.custom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.smp_auth.R;

/**
 * Run following command to trigger custom authentication login action:
 *
 * adb shell am broadcast -a com.google.example.ACTION_TOKEN --es key_token v2--Sd2T8UBqlCGQovVPnsUs4eqwFe0-1i9JV4nq__RWmsA=--dWM8r8RggUJCToOaiiT6NXmiOipkovvD9HueM_jZECStExtGFkZzVmCUhkdDJe5NQw==
 *
 * This key_token is for sample SMP user. It can be replaced by any custom provider/token.
 */
public class CustomAuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CustomAuthActivity";

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
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(mTokenReceiver, TokenBroadcastReceiver.getFilter());
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mTokenReceiver);
    }

    private void startSignIn() {
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
