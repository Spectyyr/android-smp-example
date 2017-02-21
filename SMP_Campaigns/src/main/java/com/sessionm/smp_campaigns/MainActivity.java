package com.sessionm.smp_campaigns;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.User;
import com.sessionm.api.message.data.Message;

public class MainActivity extends AppCompatActivity implements SessionListener, CampaignsFragment.OnDeepLinkTappedListener  {

    private static final String SAMPLE_USER_TOKEN = "v2--Sd2T8UBqlCGQovVPnsUs4eqwFe0-1i9JV4nq__RWmsA=--dWM8r8RggUJCToOaiiT6NXmiOipkovvD9HueM_jZECStExtGFkZzVmCUhkdDJe5NQw==";
    private TextView userBalanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        userBalanceTextView = (TextView) findViewById(R.id.user_balance_textview);
        final SessionM sessionM = SessionM.getInstance();
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
        sessionM.getCampaignsManager().fetchFeedMessages();
    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {

    }

    @Override
    public void onDeepLinkTapped(Message.MessageActionType actionType, String actionURL) {

    }
}
