/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_identity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.User;
import com.sessionm.api.message.notification.data.NotificationMessage;

public class LoginActivity extends AppCompatActivity implements SessionListener {

    private EditText _emailView;
    private EditText _passwordView;
    private Button _loginButton;

    private static final String DEBUG_EMAIL = "";
    private static final String DEBUG_PASSWORD = "";
    private static final boolean DEBUG_MODE = false;

    private static final String DEBUG_TOKEN = "v2--Sd2T8UBqlCGQovVPnsUs4eqwFe0-1i9JV4nq__RWmsA=--dWM8r8RggUJCToOaiiT6NXmiOipkovvD9HueM_jZECStExtGFkZzVmCUhkdDJe5NQw==";
    private static final String SAMPLE_TOKEN = "v2--uptXiU8SpBL-lAMK2Rvk0-qwFe0-1i9JV4nq__RWmsA=--B3Csmpxi8IQmmv59LexE6L7hoN3tscIlbA3Yjoab8Xu9pFCAHgJ-y4OXuPA_Vc-n8w==";

    private SessionM sessionM = SessionM.getInstance();

    private ProgressDialog progressDialog;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        extras = getIntent().getExtras();
        _emailView = (EditText) findViewById(R.id.sign_in_email);
        _passwordView = (EditText) findViewById(R.id.sign_in_password);
        _loginButton = (Button) findViewById(R.id.email_sign_in_button);
        Button autoTokenLoginButton = (Button) findViewById(R.id.auth_token_login_button);
        TextView autoTokenLoginWithSampleUserTextView = (TextView) findViewById(R.id.sample_auth_token_login_text);
        TextView notNowTextView = (TextView) findViewById(R.id.email_sign_in_not_now_text);

        autoTokenLoginWithSampleUserTextView.setPaintFlags(autoTokenLoginWithSampleUserTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        notNowTextView.setPaintFlags(notNowTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (DEBUG_MODE)
            _loginButton.setEnabled(true);

        _passwordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPasswordValid(s) && !_emailView.getText().toString().isEmpty()) {
                    _loginButton.setEnabled(true);
                    _loginButton.setTextColor(Color.WHITE);
                } else {
                    _loginButton.setEnabled(false);
                    _loginButton.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        _loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(_passwordView.getWindowToken(), 0);
                attemptLogin(DEBUG_MODE);
            }
        });

        progressDialog = new ProgressDialog(this);

        autoTokenLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAuthTokenLogin(DEBUG_TOKEN);
            }
        });

        autoTokenLoginWithSampleUserTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAuthTokenLogin(SAMPLE_TOKEN);
            }
        });

        notNowTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sessionM.getSessionState().equals(SessionM.State.STARTED_ONLINE) && sessionM.getUser().isRegistered()) {
            startMainActivity();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void attemptLogin(boolean debug) {
        _emailView.setError(null);
        _passwordView.setError(null);

        String email = _emailView.getText().toString();
        String password = _passwordView.getText().toString();

        if (debug) {
            email = DEBUG_EMAIL;
            password = DEBUG_PASSWORD;
        }

        boolean cancel = false;
        View focusView = null;

        if (password.isEmpty() || !isPasswordValid(password)) {
            _passwordView.setError(getString(R.string.error_invalid_password));
            focusView = _passwordView;
            cancel = true;
        }

        if (email.isEmpty()) {
            _emailView.setError(getString(R.string.error_field_required));
            focusView = _emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            _emailView.setError(getString(R.string.error_invalid_email));
            focusView = _emailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (sessionM.getSessionState().equals(SessionM.State.STARTED_ONLINE)) {
                sessionM.logInUserWithEmail(email, password);
                progressDialog.setTitle(R.string.logging_in);
                progressDialog.show();
            } else
                Toast.makeText(this, "Network Error!", Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptAuthTokenLogin(String token) {
        sessionM.authenticateWithToken("api_auth_token", token);
    }

    @Override
    public void onSessionStateChanged(SessionM sessionM, SessionM.State state) {
        if (state.equals(SessionM.State.STARTED_ONLINE) && sessionM.getUser().isRegistered()) {
            startMainActivity();
        }
    }

    @Override
    public void onSessionFailed(SessionM sessionM, int i) {

    }

    @Override
    public void onUserUpdated(SessionM sessionM, User user) {
        if (user != null) {
            progressDialog.dismiss();
            SessionM.EnrollmentResultType resultType = SessionM.getInstance().getEnrollmentResult();
            if (resultType.equals(SessionM.EnrollmentResultType.SUCCESS)) {
                Toast.makeText(this, "Success: " + user.toString(), Toast.LENGTH_SHORT).show();
                startMainActivity();
            } else if (resultType.equals(SessionM.EnrollmentResultType.FAILURE)) {
                Toast.makeText(this, "Failed: " + SessionM.getInstance().getResponseErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {

    }

    @Override
    public void onNotificationMessage(SessionM sessionM, NotificationMessage message) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isEmailValid(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isPasswordValid(CharSequence target) {
        return target.length() > 0;
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        if (extras != null)
            intent.putExtras(extras);
        startActivity(intent);
        finishAffinity();
    }
}



