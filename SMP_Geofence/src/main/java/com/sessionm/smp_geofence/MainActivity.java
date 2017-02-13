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

import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.User;
import com.sessionm.api.geofence.GeofenceListener;
import com.sessionm.api.geofence.GeofenceManager;
import com.sessionm.api.geofence.data.GeofenceEvent;
import com.sessionm.api.geofence.data.TriggeredEvent;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements SessionListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String SAMPLE_USER_TOKEN = "v2--Sd2T8UBqlCGQovVPnsUs4eqwFe0-1i9JV4nq__RWmsA=--dWM8r8RggUJCToOaiiT6NXmiOipkovvD9HueM_jZECStExtGFkZzVmCUhkdDJe5NQw==";
    private TextView userBalanceTextView;
    private SessionM sessionM = SessionM.getInstance();

    ViewPager viewPager;

    ToggleButton geofenceToggleButton;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        viewPager = (ViewPager) findViewById(R.id.main_pager);
        viewPager.setAdapter(new SMPagerAdapter(getSupportFragmentManager()));

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

        geofenceToggleButton = (ToggleButton) findViewById(R.id.geofence_toggle);
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

        final ToggleButton tabsToggleButton = (ToggleButton) findViewById(R.id.tabs_toggle);
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
    protected void onDestroy() {
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
    public void onSessionStateChanged(SessionM sessionM, SessionM.State state) {
        if (state.equals(SessionM.State.STARTED_ONLINE))
            sessionM.authenticateWithToken("auth_token", SAMPLE_USER_TOKEN);
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
