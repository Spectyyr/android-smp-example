package com.sessionm.smp_geofence;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.geofence.api.GeofenceListener;
import com.sessionm.geofence.api.GeofenceManager;
import com.sessionm.geofence.api.data.GeofenceEvent;
import com.sessionm.geofence.api.data.TriggeredEvent;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.provider.SessionMOauthEmailProvider;
import com.sessionm.identity.api.provider.SessionMOauthProvider;

import java.util.List;
import java.util.Set;

import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";
    private TextView userBalanceTextView;
    private SessionMOauthEmailProvider _sessionMOauthEmailProvider;
    private UserManager _userManager = UserManager.getInstance();

    ViewPager viewPager;

    ToggleButton geofenceToggleButton;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);
        _sessionMOauthEmailProvider = new SessionMOauthEmailProvider();
        SessionM.setAuthenticationProvider(_sessionMOauthEmailProvider, null);

        viewPager = findViewById(R.id.main_pager);
        viewPager.setAdapter(new SMPagerAdapter(getSupportFragmentManager()));

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

        geofenceToggleButton = findViewById(R.id.geofence_toggle);
        final GeofenceManager geofenceManager = GeofenceManager.getInstance(this);
        boolean geofenceServiceIsStarted = geofenceManager.isStarted();
        //TODO: always start geofence again
        if (geofenceServiceIsStarted)
            geofenceManager.startGeofenceService(geofenceListener);
        geofenceToggleButton.setChecked(geofenceServiceIsStarted);

        geofenceToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Sets debug mode. If this is enabled, a local push notification will be sent to push notification center when geofence event is triggered.
                    geofenceManager.setDebugMode(true);
                    /* Explicitly set the fastest interval for location updates, in milliseconds. Default is 30s.
                     * This controls the fastest rate at which your application will receive location updates, which might be faster than setInterval(long) in some situations
                     * (for example, if other applications are triggering location updates).
                     * This allows your application to passively acquire locations at a rate faster than it actively acquires locations, saving power.
                     * Unlike setInterval(long), this parameter is exact. Your application will never receive updates faster than this value.
                     * If you don't call this method, a fastest interval will be selected for you. It will be a value faster than your active interval (setInterval(long)).
                     * An interval of 0 is allowed, but not recommended, since location updates may be extremely fast on future implementations.
                     * If setFastestInterval(long) is set slower than setInterval(long), then your effective fastest interval is setInterval(long).
                     */
                    geofenceManager.setFastestLocationUpdateInterval(15 * 1000);
                    /* Set the desired interval for active location updates, in milliseconds. Default is 180s.
                     * The location client will actively try to obtain location updates for your application at this interval, so it has a direct influence on the amount of power used by your application.
                     * Choose your interval wisely. This interval is inexact. You may not receive updates at all (if no location sources are available),
                     * or you may receive them slower than requested. You may also receive them faster than requested (if other applications are requesting location at a faster interval).
                     * Applications with only the coarse location permission may have their interval silently throttled. */
                    geofenceManager.setLocationUpdateInterval(60 * 1000);
                    geofenceManager.startGeofenceService(geofenceListener);
                } else {
                    geofenceManager.stopGeofenceService();
                }
            }
        });

        if (geofenceServiceIsStarted) {
            RxBus.getInstance().setGeofenceList(geofenceManager.getGeofenceEventsList());
        }

        subscription = RxBus.getInstance().getStringObservable().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        final ToggleButton tabsToggleButton = findViewById(R.id.tabs_toggle);
        int currentPage = viewPager.getCurrentItem();
        if (currentPage == 0) {
            tabsToggleButton.setChecked(false);
        } else if (currentPage == 1) {
            tabsToggleButton.setChecked(true);
        }
        tabsToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    viewPager.setCurrentItem(1);
                } else {
                    viewPager.setCurrentItem(0);
                }
            }
        });
        UserManager.getInstance().fetchUser();
    }

    GeofenceListener geofenceListener = new GeofenceListener() {
        @Override
        public void onGeofenceEventsUpdated(List<GeofenceEvent> list) {
            RxBus.getInstance().setString("Geofence events list updated!");
            RxBus.getInstance().setGeofenceList(list);
            RxBus.getInstance().setLog(new GeofenceLog("Geofence events list updated.", ""));
        }

        @Override
        public void onGeofenceEventTriggered(TriggeredEvent triggeredEvent) {
            RxBus.getInstance().setLog(new GeofenceLog("Name: " + triggeredEvent.getGeofenceEvent().getEventName()
                    , "Distance: " + triggeredEvent.getGeofenceEvent().getLocation().distanceTo(triggeredEvent.getTriggeredLocation()) + "\n"
                    + "Triggered: " + triggeredEvent.getGeofenceEvent().getTriggerType().toString() + "\n"
                    + "ID: " + triggeredEvent.getGeofenceEvent().getID()));
        }

        @Override
        public void onGeofenceServiceStarted() {

        }

        @Override
        public void onGeofenceServiceStopped() {
            RxBus.getInstance().setGeofenceList(null);
            geofenceToggleButton.setChecked(false);
        }

        @Override
        public void onError(SessionMError error) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        GeofenceManager.getInstance(this).setGeofenceListener(geofenceListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscription.unsubscribe();
    }

    private class SMPagerAdapter extends FragmentPagerAdapter {

        public SMPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MapFragment.newInstance("mapFragment");
                case 1:
                    return LogsFragment.newInstance("logsFragment");
                default:
                    return LogsFragment.newInstance("logsFragment");
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
