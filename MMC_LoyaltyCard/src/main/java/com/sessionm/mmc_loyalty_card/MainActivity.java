package com.sessionm.mmc_loyalty_card;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.User;
import com.sessionm.api.message.notification.data.NotificationMessage;

public class MainActivity extends AppCompatActivity implements SessionListener {

    private static final String SAMPLE_USER_TOKEN = "v2--uptXiU8SpBL-lAMK2Rvk0-qwFe0-1i9JV4nq__RWmsA=--B3Csmpxi8IQmmv59LexE6L7hoN3tscIlbA3Yjoab8Xu9pFCAHgJ-y4OXuPA_Vc-n8w==";

    private TextView userBalanceTextView;
    private FloatingActionButton linkCardButton;

    private SessionM sessionM = SessionM.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        linkCardButton = (FloatingActionButton) findViewById(R.id.action_link_card);
        linkCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoyaltyCardActivity.class));
            }
        });

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
        sessionM.getReceiptsManager().fetchReceipts();
    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {

    }

    @Override
    public void onNotificationMessage(SessionM sessionM, NotificationMessage notificationMessage) {

    }
}
