/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.User;
import com.sessionm.api.identity.IdentityListener;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.data.SMSVerification;
import com.sessionm.api.message.data.Message;
import com.sessionm.mmc.R;

public class LoginActivity extends AppCompatActivity implements SessionListener {

    private static final String DEFAULT_CODE_MESSAGE = "Welcome to SessionM Rewards! Here is your activation code: {code}";
    protected EditText _emailView;
    protected EditText _passwordView;
    protected Button _loginButton;

    private static final String DEBUG_EMAIL = "";
    private static final String DEBUG_PASSWORD = "";
    private static final boolean DEBUG_MODE = false;

    private SessionM sessionM = SessionM.getInstance();

    private ProgressDialog progressDialog;

    private IdentityManager identityManager = new IdentityManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailView = (EditText) findViewById(R.id.sign_in_email);
        _passwordView = (EditText) findViewById(R.id.sign_in_password);
        _loginButton = (Button) findViewById(R.id.email_sign_in_button);
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
                String email = _emailView.getText().toString();
                attemptLogin(email, DEBUG_MODE);
            }
        });

        progressDialog = new ProgressDialog(this);

        identityManager.setListener(_identityListener);
    }

    IdentityListener _identityListener = new IdentityListener() {
        @Override
        public void onSMSVerificationMessageSent(SMSVerification smsVerification) {
            popUpSMSVerificationDialog("verify_code");
        }

        @Override
        public void onSMSVerificationCodeChecked(SMSVerification smsVerification) {
            Toast.makeText(LoginActivity.this, smsVerification.getString(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finishAffinity();
        }

        @Override
        public void onSMSVerificationFetched(SMSVerification smsVerification) {
            if (smsVerification.isValid()) {
                Toast.makeText(LoginActivity.this, smsVerification.getString(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finishAffinity();
            } else {
                popUpSMSVerificationDialog("send_code");
            }

        }

        @Override
        public void onFailure(SessionMError error) {
            Toast.makeText(LoginActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        identityManager.setListener(_identityListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        identityManager.setListener(null);
    }

    public void attemptLogin(String email, boolean debug) {
        _emailView.setError(null);
        _passwordView.setError(null);

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

    @Override
    public void onSessionStateChanged(SessionM sessionM, SessionM.State state) {
        if (state.equals(SessionM.State.STARTED_ONLINE) && sessionM.getUser().isRegistered()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finishAffinity();
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
                identityManager.fetchSMSVerification();
            } else if (resultType.equals(SessionM.EnrollmentResultType.FAILURE)) {
                Toast.makeText(this, "Failed: " + SessionM.getInstance().getResponseErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {

    }

    @Override
    public void onNotificationMessage(SessionM sessionM, Message message) {

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

    protected void popUpSMSVerificationDialog(final String type) {

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_sms_validation, null);
        TextView descriptionTextView = (TextView) dialogLayout.findViewById(R.id.sms_validation_description);
        final EditText inputEditText = (EditText) dialogLayout.findViewById(R.id.sms_validation_edittext);

        if (type.equals("send_code")) {
            descriptionTextView.setText("Please enter your phone number.");
            inputEditText.setHint("Phone Number");
        } else if (type.equals("verify_code")) {
            descriptionTextView.setText("Please enter your verification code.");
            inputEditText.setHint("Code");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type.equals("send_code")) {
                    String phone = inputEditText.getText().toString();
                    identityManager.sendSMSVerificationMessage(phone, DEFAULT_CODE_MESSAGE);
                } else if (type.equals("verify_code")) {
                    String code = inputEditText.getText().toString();
                    identityManager.checkSMSVerificationCode(code);
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setView(dialogLayout);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }
}



