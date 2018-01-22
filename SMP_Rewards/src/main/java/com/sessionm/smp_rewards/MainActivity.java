package com.sessionm.smp_rewards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.User;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.reward.RewardsManager;

import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SessionListener {

    //    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";
    private static final String SAMPLE_USER_TOKEN_US = "KKcShUjSz4SZOSpn1AkeqGgVQ8mzp0ZyoGhsZaOKmLoOfpjKwZTJ7ULfcmh+1kUvvPy433+xSKM/5FiOAyr04A==";
    private static final String SAMPLE_USER_TOKEN_CAN = "jpW0O4TlIKC+1+b/tqvMsxJgC0dr9MHxU/uMcnt1Uf5ifmlS/gtofgrzNBK3Kt8o/NBbj3ti5W7ZzbZ9O/AqAQ==";

    private TextView userBalanceTextView;
    private TextView ordersTextView;

    private SessionM sessionM = SessionM.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        userBalanceTextView = (TextView) findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN_US);
//                if (!sessionM.getUser().isRegistered())
//                    IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
//                else
//                    IdentityManager.getInstance().logOutUser();
            }
        });

        ordersTextView = (TextView) findViewById(R.id.orders_textview);
        ordersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrdersActivity.class));
            }
        });
        sessionM.setSessionListener(this);
        restartSessionM();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserManager.getInstance().setListener(_userListener);
    }

    UserListener _userListener = new UserListener() {
        @Override
        public void onUserUpdated(SMPUser smpUser, Set<String> set) {
            if (smpUser != null) {
                userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
            } else
                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
            RewardsManager.getInstance().fetchOffers();
        }

        @Override
        public void onFailure(SessionMError sessionMError) {

        }
    };

    private void restartSessionM() {
        Locale locale = Locale.getDefault();
        Log.d("SessionM.Locale!!!", locale.getDisplayName());
        if (locale.equals(Locale.CANADA) || locale.equals(Locale.CANADA_FRENCH)) {
            sessionM.startWithConfigFile("SMPConfig-CAN", getApplication());
        } else {
            sessionM.startWithConfigFile(getApplication());
        }
    }

    private void authenticateUser() {
        Locale locale = Locale.getDefault();
        if (locale.equals(Locale.CANADA) || locale.equals(Locale.CANADA_FRENCH)) {
            IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN_CAN);
        } else {
            IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN_US);
        }
    }

    @Override
    public void onSessionStateChanged(SessionM sessionM, SessionM.State state) {
        if (state == SessionM.State.STARTED_ONLINE) {
            authenticateUser();
        }
    }

    @Override
    public void onSessionFailed(SessionM sessionM, int i) {

    }

    @Override
    public void onUserUpdated(SessionM sessionM, User user) {

    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {

    }
}
