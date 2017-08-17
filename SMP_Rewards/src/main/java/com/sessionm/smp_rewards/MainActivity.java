package com.sessionm.smp_rewards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.reward.RewardsManager;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA3LTE0IDE4OjM4OjIwICswMDAwIiwiZXhwIjoiMjAxNy0wNy0yOCAxODozODoyMCArMDAwMCJ9.wXLHwQYWtfXA4_Kn4mBrdPXFsMvrCdHaLr4GK67CoPUx3jDwKXX4Wg0HPDjY5RFPzLdOAZGnPXhSna0rVkIkxEzEi0I6gzx_6CggUluxMJnDMUW5HHG0yo040e6tgqIl99VAZZZFbIwCF7qiDnIH01H7IdZz8e0uokq2TaHTKLoo16sUJCJIgSNfOkaRfS9uvlcwFftdH-wqZl5KZ3kUqscAW0lqEVcLdxUaA76Oc0bUFEuvpIRX7iWzAM-nIZcLPCCpRqtqaN3LnuorMxytcgYNUmec6F5228wK7X1mN3C8NbMD24SHRQnVtV4hsTNzycA23CnlwjZJhiye4n7FqQ";

    private TextView userBalanceTextView;
    private TextView ordersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        final SessionM sessionM = SessionM.getInstance();
        userBalanceTextView = (TextView) findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sessionM.getUser().isRegistered())
                    IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
                else
                    IdentityManager.getInstance().logOutUser();
            }
        });

        ordersTextView = (TextView) findViewById(R.id.orders_textview);
        ordersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrdersActivity.class));
            }
        });
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
}
