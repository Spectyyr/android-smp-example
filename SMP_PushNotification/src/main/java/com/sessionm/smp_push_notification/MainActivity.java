package com.sessionm.smp_push_notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.User;
import com.sessionm.api.message.MessagesListener;
import com.sessionm.api.message.data.Message;
import com.sessionm.api.message.notification.data.NotificationMessage;

public class MainActivity extends AppCompatActivity implements SessionListener {

    private static final String SAMPLE_USER_TOKEN = "v2--Sd2T8UBqlCGQovVPnsUs4eqwFe0-1i9JV4nq__RWmsA=--dWM8r8RggUJCToOaiiT6NXmiOipkovvD9HueM_jZECStExtGFkZzVmCUhkdDJe5NQw==";

    private TextView userBalanceTextView;
    private ToggleButton useBundleExtrasButton;
    private NotificationMessage pushMessage;
    private SessionM sessionM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionM = SessionM.getInstance();

        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        userBalanceTextView = (TextView) findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sessionM.getUser().isRegistered())
                    sessionM.authenticateWithToken("auth_token", SAMPLE_USER_TOKEN);
                else
                    sessionM.logOutUser();
            }
        });

        useBundleExtrasButton = (ToggleButton) findViewById(R.id.use_bundle_extras_toggle);
        useBundleExtrasButton.setChecked(sessionM.getMessageManager().isUseBundleExtrasEnabled());
        useBundleExtrasButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sessionM.getMessageManager().setUseBundleExtrasEnabled(isChecked);
            }
        });

        //Only needed when use bundle extras enabled.
        if (sessionM.getMessageManager().isUseBundleExtrasEnabled()) {
            Bundle extras = getIntent().getExtras();
            pushMessage = sessionM.getMessageManager().getPendingNotification(extras);
            setUpPushMessaging();
        } else {
            Intent intent = getIntent();
            if (intent.getAction().equals(Intent.ACTION_VIEW)) {
                String url = intent.getData().toString();
                handleDeepLinkString(url);
            }
        }
    }

    @Override
    public void onSessionStateChanged(SessionM sessionM, SessionM.State state) {

    }

    @Override
    public void onSessionFailed(SessionM sessionM, int i) {

    }

    @Override
    public void onUserUpdated(SessionM sessionM, User user) {
        if (user.isRegistered())
            userBalanceTextView.setText(user.getPointBalance() + "pts");
        else
            userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {

    }

    public void TriggerOpenAdPush(View view) {
        sessionM.logAction("push_notification_open_ad");
    }

    public void TriggerDeepLinkPush(View view) {
        sessionM.logAction("push_notification_deep_link");
    }

    public void TriggerExternalLinkPush(View view) {
        sessionM.logAction("push_notification_external_link");
    }

    //Only needed when use bundle extras enabled.
    @Override
    protected void onResume() {
        super.onResume();
        if (pushMessage != null) {
            sessionM.getMessageManager().executePendingNotificationFromPush(pushMessage);
        }
    }

    //Only needed when use bundle extras enabled.
    private void setUpPushMessaging() {
        SessionM.getInstance().getMessageManager().setListener(new MessagesListener() {
            @Override
            public void onNotificationMessage(NotificationMessage notificationMessage) {
                handleMessageAction(notificationMessage);
                pushMessage = null;
                Toast.makeText(MainActivity.this, "New push notification received!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(SessionMError sessionMError) {

            }
        });
    }

    private void handleDeepLinkString(String url) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deep Link").setMessage(url);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Only needed when use bundle extras enabled.
    private void handleMessageAction(NotificationMessage message) {
        // Optionally check message type
        if (message == null)
            return;
        if (message.getActionType() == Message.MessageActionType.EXTERNAL_LINK) {
            // Launch URL in Mobile Browser
            // Provide sample code
        } else if (message.getActionType() == Message.MessageActionType.DEEP_LINK) {
            // handle navigation to deep line
        } else {
            // Log Error
        }
    }
}
