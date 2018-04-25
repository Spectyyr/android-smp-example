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

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.event.api.EventsManager;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.provider.SessionMOauthEmailProvider;
import com.sessionm.identity.api.provider.SessionMOauthProvider;
import com.sessionm.message.api.MessagesListener;
import com.sessionm.message.api.MessagesManager;
import com.sessionm.message.api.data.NotificationMessage;

import java.util.Set;

import static com.sessionm.message.api.data.NotificationMessage.ActionType.DEEP_LINK;
import static com.sessionm.message.api.data.NotificationMessage.ActionType.EXTERNAL_LINK;

public class MainActivity extends AppCompatActivity {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";

    private TextView userBalanceTextView;
    private ToggleButton useBundleExtrasButton;
    private NotificationMessage pushMessage;
    private SessionMOauthEmailProvider _sessionMOauthEmailProvider;
    private MessagesManager _messageManager = MessagesManager.getInstance();
    private UserManager _userManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);
        _sessionMOauthEmailProvider = new SessionMOauthEmailProvider();
        SessionM.setAuthenticationProvider(_sessionMOauthEmailProvider, null);

        userBalanceTextView = findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_userManager.getCurrentUser() == null)
                    _sessionMOauthEmailProvider.authenticateUser("test@sessionm.com", "aaaaaaaa1", new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (sessionMError != null) {
                                Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                _userManager.fetchUser(new UserManager.OnUserFetchedListener() {
                                    @Override
                                    public void onFetched(SMPUser smpUser, Set<String> set, SessionMError sessionMError) {
                                        if (sessionMError != null) {
                                            Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (smpUser != null) {
                                                userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
                                            } else
                                                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
                                        }
                                    }
                                });
                            }
                        }
                    });
                else
                    _sessionMOauthEmailProvider.logoutUser(new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (authenticatedState.equals(SessionMOauthProvider.AuthenticatedState.NotAuthenticated))
                                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
                        }
                    });
            }
        });

        useBundleExtrasButton = findViewById(R.id.use_bundle_extras_toggle);
        useBundleExtrasButton.setChecked(_messageManager.isUseBundleExtrasEnabled());
        useBundleExtrasButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _messageManager.setUseBundleExtrasEnabled(isChecked);
            }
        });

        //Only needed when use bundle extras enabled.
        if (_messageManager.isUseBundleExtrasEnabled()) {
            Bundle extras = getIntent().getExtras();
            pushMessage = _messageManager.getPendingNotification(extras);
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

    public void TriggerOpenAppPush(View view) {
        EventsManager.getInstance().postEvent(new EventsManager.SimpleEventBuilder("push_open_property").build());
    }

    public void TriggerDeepLinkPush(View view) {
        EventsManager.getInstance().postEvent(new EventsManager.SimpleEventBuilder("push_notification_deep_link").build());
    }

    public void TriggerExternalLinkPush(View view) {
        EventsManager.getInstance().postEvent(new EventsManager.SimpleEventBuilder("push_notification_external_link").build());
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
            _messageManager.executePendingNotificationFromPush(pushMessage);
        }
    }

    private void setUpPushMessaging() {
        _messageManager.setListener(new MessagesListener() {
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