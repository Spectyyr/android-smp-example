package com.sessionm.smp_offers;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.provider.SessionMOauthProvider;
import com.sessionm.smp_offers.my_offers.MyOffersFragment;
import com.sessionm.smp_offers.store_offers.StoreOffersFragment;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements StoreOffersFragment.Callback {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";
    private static final String SAMPLE_USER_EMAIL = "sampleuser@sessionm.com";
    private static final String SAMPLE_USER_PWD = "sessionm1";
    private final UserManager _userManager = UserManager.getInstance();

    private Button authenticate;
    private TextView userBalance;
    private TabLayout _tabs;
    private ViewPager _pager;
    private OffersPagerAdapter _adapter;
    private SessionMOauthProvider _sessionMOauthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userBalance = findViewById(R.id.user_balance);
        _sessionMOauthProvider = (SessionMOauthProvider) SessionM.getAuthenticationProvider();
        authenticate = findViewById(R.id.authenticate);
        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_userManager.getCurrentUser() == null) {
                    authenticateUser();
                } else {
                    logoutUser();
                }
            }
        });

        _tabs = findViewById(R.id.tabs);
        _pager = findViewById(R.id.pager);
        _pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                fetchForPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        _adapter = new OffersPagerAdapter(getSupportFragmentManager(), _myOffersFragment, _storeOffersFragment);
        _pager.setAdapter(_adapter);
        _tabs.setupWithViewPager(_pager);

    }

    @Override
    public void updatePoints(int points) {
        userBalance.setText(points + " pts");
    }

    private void fetchForPage(int position) {
        switch (position) {
            case 0:
                _myOffersFragment.fetchOffers();
                break;
            case 1:
                _storeOffersFragment.fetchOffers();
                break;
            default:
                Log.d("TAG", "Too Many Tabs");
        }
    }

    private MyOffersFragment _myOffersFragment = new MyOffersFragment();
    private StoreOffersFragment _storeOffersFragment = new StoreOffersFragment();

    @Override
    protected void onResume() {
        super.onResume();
        if (_sessionMOauthProvider.isAuthenticated())
            fetchUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _userManager.setListener(null);
    }

    private void authenticateUser() {
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
    }

    private void fetchUser() {
        _userManager.fetchUser(new UserManager.OnUserFetchedListener() {
            @Override
            public void onFetched(SMPUser smpUser, Set<String> set, SessionMError sessionMError) {
                if (sessionMError != null) {
                    Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (smpUser != null) {
                        updatePoints(smpUser.getAvailablePoints());
                        authenticate.setText("Logout");
                        fetchForPage(_pager.getCurrentItem());
                    } else {
                        userBalance.setText("");
                        authenticate.setText("Login");
                    }
                }
            }
        });
    }

    private void logoutUser() {
        _sessionMOauthProvider.logoutUser(new SessionMOauthProvider.SessionMOauthProviderListener() {
            @Override
            public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                if (authenticatedState.equals(SessionMOauthProvider.AuthenticatedState.NotAuthenticated)) {
                    userBalance.setText("");
                    authenticate.setText("Login");
                }
            }
        });
    }
}
