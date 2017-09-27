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

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.message.MessagesListener;
import com.sessionm.api.message.notification.data.NotificationMessage;

import java.util.Set;

import static com.sessionm.api.message.notification.data.NotificationMessage.ActionType.DEEP_LINK;
import static com.sessionm.api.message.notification.data.NotificationMessage.ActionType.EXTERNAL_LINK;

public class MainActivity extends AppCompatActivity {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";

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
                if (UserManager.getInstance().getCurrentUser() == null)
                    IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
                else
                    IdentityManager.getInstance().logOutUser();
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
        }
        //Handle deep link, if needed
        else {
            Intent intent = getIntent();
            if (intent.getAction().equals(Intent.ACTION_VIEW)) {
                String url = intent.getData().toString();
                handleDeepLinkString(url);
            }
        }
    }

    //User has to be authenticated in order to receive a push
    UserListener _userListener = new UserListener() {
        @Override
        public void onUserUpdated(SMPUser smpUser, Set<String> set) {
            if (smpUser != null) {
                userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
            } else
                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
        }

        @Override
        public void onFailure(SessionMError sessionMError) {

        }
    };

    public void TriggerOpenAppPush(View view) {
        sessionM.logAction("push_open_property");
    }

    public void TriggerDeepLinkPush(View view) {
        sessionM.logAction("push_notification_deep_link");
    }

    public void TriggerExternalLinkPush(View view) {
        sessionM.logAction("push_notification_external_link");
    }

    private void handleDeepLinkString(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deep Link").setMessage(url);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //************************The following code is only needed when use bundle extras enabled.********************//
    @Override
    protected void onResume() {
        super.onResume();
        if (pushMessage != null) {
            sessionM.getMessageManager().executePendingNotificationFromPush(pushMessage);
        }
        UserManager.getInstance().setListener(_userListener);
    }

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

    private void handleMessageAction(NotificationMessage message) {
        // Optionally check message type
        if (message == null)
            return;
        if (message.getActionType() == EXTERNAL_LINK) {
            // Launch URL in Mobile Browser
            // Provide sample code
        } else if (message.getActionType() == DEEP_LINK) {
            // handle navigation to deep line
        } else {
            // Log Error
        }
    }
}