package com.sessionm.smp_places;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.place.PlacesListener;
import com.sessionm.api.place.PlacesManager;
import com.sessionm.api.place.data.CheckinResult;
import com.sessionm.api.place.data.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA3LTE0IDE4OjM4OjIwICswMDAwIiwiZXhwIjoiMjAxNy0wNy0yOCAxODozODoyMCArMDAwMCJ9.wXLHwQYWtfXA4_Kn4mBrdPXFsMvrCdHaLr4GK67CoPUx3jDwKXX4Wg0HPDjY5RFPzLdOAZGnPXhSna0rVkIkxEzEi0I6gzx_6CggUluxMJnDMUW5HHG0yo040e6tgqIl99VAZZZFbIwCF7qiDnIH01H7IdZz8e0uokq2TaHTKLoo16sUJCJIgSNfOkaRfS9uvlcwFftdH-wqZl5KZ3kUqscAW0lqEVcLdxUaA76Oc0bUFEuvpIRX7iWzAM-nIZcLPCCpRqtqaN3LnuorMxytcgYNUmec6F5228wK7X1mN3C8NbMD24SHRQnVtV4hsTNzycA23CnlwjZJhiye4n7FqQ";
    private TextView userBalanceTextView;
    private ToggleButton showMarkerToggle;
    private GoogleMap mMap;
    private SessionM sessionM = SessionM.getInstance();
    private PlacesManager placesManager;
    Map<String, Place> placesMap = new HashMap<>();

    List<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placesManager = sessionM.getPlacesManager();
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
                if (UserManager.getInstance().getCurrentUser() == null)
                    IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
                else
                    IdentityManager.getInstance().logOutUser();
            }
        });

        showMarkerToggle = (ToggleButton) findViewById(R.id.show_marker_toggle);
        showMarkerToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (Marker marker : markers)
                    marker.setVisible(isChecked);
            }
        });

    }

    PlacesListener _placesListener = new PlacesListener() {
        @Override
        public void onPlacesFetched(List<Place> list) {
            mMap.clear();
            markers.clear();
            placesMap.clear();
            for (Place place : list) {
                LatLng latLng = new LatLng(place.getLat(), place.getLng());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(place.getName())
                        .snippet(place.getID()));
                markers.add(marker);
                placesMap.put(place.getID(), place);
            }
            showMarkerToggle.setChecked(true);
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
        UserManager.getInstance().setListener(_userListener);
    }

    UserListener _userListener = new UserListener() {
        @Override
        public void onUserUpdated(SMPUser smpUser, Set<String> set) {
            if (smpUser != null) {
                userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
            } else
                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
        }

        @Override
        public void onFailure(SessionMError sessionMError) {

        }
    };

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
}
