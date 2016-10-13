package com.sessionm.mmc_places;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.User;
import com.sessionm.api.message.notification.data.NotificationMessage;
import com.sessionm.api.place.PlacesListener;
import com.sessionm.api.place.PlacesManager;
import com.sessionm.api.place.data.CheckinResult;
import com.sessionm.api.place.data.Place;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SessionListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String SAMPLE_USER_TOKEN = "v2--uptXiU8SpBL-lAMK2Rvk0-qwFe0-1i9JV4nq__RWmsA=--B3Csmpxi8IQmmv59LexE6L7hoN3tscIlbA3Yjoab8Xu9pFCAHgJ-y4OXuPA_Vc-n8w==";
    private TextView userBalanceTextView;
    private GoogleMap mMap;
    private SessionM sessionM = SessionM.getInstance();
    private PlacesManager placesManager;
    Map<String, Place> placesMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placesManager = sessionM.getPlacesManager();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
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
    }

    PlacesListener _placesListener = new PlacesListener() {
        @Override
        public void onPlacesFetched(List<Place> list) {
            mMap.clear();
            placesMap.clear();
            for (Place place : list) {
                LatLng latLng = new LatLng(place.getLat(), place.getLng());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(place.getName())
                        .snippet(place.getID()));
                placesMap.put(place.getID(), place);
            }
        }

        @Override
        public void onCheckedIn(CheckinResult checkinResult) {
            Toast.makeText(MainActivity.this, "Succuss! Can check in again at: " + checkinResult.getCanCheckinAgainAt(), Toast.LENGTH_LONG).show();
            if (checkinResult.getMessage() != null)
                sessionM.presentActivity(SessionM.ActivityType.PORTAL, checkinResult.getMessage().getActionURL());
        }

        @Override
        public void onFailure(SessionMError sessionMError) {
            Toast.makeText(MainActivity.this, "Failed! " + sessionMError.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        placesManager.setListener(_placesListener);
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
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    placesManager.fetchPlaces(mMap.getMyLocation());
                    return false;
                }
            });
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Place currentPlace = placesMap.get(marker.getSnippet());
                    if (currentPlace.getCheckinStatus().equals(Place.CheckinStatus.CHECKABLE))
                        placesManager.checkIn(currentPlace);
                    else
                        Toast.makeText(MainActivity.this, "Unable to check in! " + currentPlace.getCheckinStatus(), Toast.LENGTH_LONG).show();
                }
            });
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

    @Override
    public void onNotificationMessage(SessionM sessionM, NotificationMessage notificationMessage) {

    }
}
