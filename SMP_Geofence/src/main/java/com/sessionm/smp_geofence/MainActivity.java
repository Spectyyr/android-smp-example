package com.sessionm.smp_geofence;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.User;
import com.sessionm.api.geofence.GeofenceListener;
import com.sessionm.api.geofence.GeofenceManager;
import com.sessionm.api.geofence.data.GeofenceEvent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SessionListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String SAMPLE_USER_TOKEN = "v2--Sd2T8UBqlCGQovVPnsUs4eqwFe0-1i9JV4nq__RWmsA=--dWM8r8RggUJCToOaiiT6NXmiOipkovvD9HueM_jZECStExtGFkZzVmCUhkdDJe5NQw==";
    private TextView userBalanceTextView;
    private ToggleButton showCircleToggle;
    private GoogleMap mMap;
    private SessionM sessionM = SessionM.getInstance();

    List<Circle> circles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

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

        final ToggleButton geofenceToggleButton = (ToggleButton) findViewById(R.id.geofence_toggle);
        geofenceToggleButton.setChecked(GeofenceManager.isStarted(getApplicationContext()));
        geofenceToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GeofenceManager.setDebugMode(getApplicationContext(), true);
                    GeofenceManager.startGeofenceService(getApplicationContext(), new GeofenceListener() {
                        @Override
                        public void onGeofenceEventsUpdated(List<GeofenceEvent> list) {
                            mMap.clear();
                            circles.clear();
                            for (GeofenceEvent geofenceEvent : list) {
                                LatLng latLng = new LatLng(geofenceEvent.getLatitude(), geofenceEvent.getLongitude());
                                Circle circle = mMap.addCircle(new CircleOptions()
                                        .center(latLng)
                                        .radius(geofenceEvent.getRadius())
                                        .fillColor(Color.RED));
                                circles.add(circle);
                            }
                            showCircleToggle.setChecked(true);
                        }

                        @Override
                        public void onGeofenceServiceStarted() {

                        }

                        @Override
                        public void onGeofenceServiceStopped() {
                            geofenceToggleButton.setChecked(false);
                        }

                        @Override
                        public void onError() {

                        }
                    });
                } else {
                    GeofenceManager.stopGeofenceService(getApplicationContext());
                    mMap.clear();
                    circles.clear();
                }
            }
        });

        showCircleToggle = (ToggleButton) findViewById(R.id.show_circle_toggle);
        showCircleToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (Circle circle : circles)
                    circle.setVisible(isChecked);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
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
    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {

    }
}
