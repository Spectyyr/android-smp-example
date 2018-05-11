package com.sessionm.smp_transactions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.provider.SessionMOauthProvider;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";
    private static final String SAMPLE_USER_EMAIL = "sampleuser@sessionm.com";
    private static final String SAMPLE_USER_PWD = "sessionm1";
    private TextView userBalanceTextView;
    private SessionMOauthProvider _sessionMOauthProvider;
    private UserManager _userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        _sessionMOauthProvider = (SessionMOauthProvider) SessionM.getAuthenticationProvider();
        _userManager = UserManager.getInstance();

        userBalanceTextView = findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_userManager.getCurrentUser() == null)
                    _sessionMOauthProvider.authenticateUser(SAMPLE_USER_EMAIL, SAMPLE_USER_PWD, new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (sessionMError != null) {
                                Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                fetchUser();
                            }
                        }
                    });
                else
                    _sessionMOauthProvider.logoutUser(new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (authenticatedState.equals(SessionMOauthProvider.AuthenticatedState.NotAuthenticated)) {
                                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
                                refreshUI();
                            }
                        }
                    });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
        if (_sessionMOauthProvider.isAuthenticated())
            fetchUser();
    }

    private void fetchUser() {
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
                refreshUI();
            }
        });
    }

    private void refreshUI() {
        TransactionsFragment f = (TransactionsFragment) getSupportFragmentManager().findFragmentById(R.id.transactions_fragment);
        f.onRefresh();
    }
}
