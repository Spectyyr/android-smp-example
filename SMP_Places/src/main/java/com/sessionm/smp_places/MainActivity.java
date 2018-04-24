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
import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.provider.SessionMOauthEmailProvider;
import com.sessionm.identity.api.provider.SessionMOauthProvider;
import com.sessionm.place.api.PlacesManager;
import com.sessionm.place.api.data.CheckinResult;
import com.sessionm.place.api.data.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";
    private TextView userBalanceTextView;
    private ToggleButton showMarkerToggle;
    private GoogleMap mMap;
    private PlacesManager _placesManager = PlacesManager.getInstance();
    Map<String, Place> placesMap = new HashMap<>();
    private SessionMOauthEmailProvider _sessionMOauthEmailProvider;
    private UserManager _userManager;
    List<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar actionBar = findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        _sessionMOauthEmailProvider = new SessionMOauthEmailProvider();
        SessionM.setAuthenticationProvider(_sessionMOauthEmailProvider, null);
        _userManager = UserManager.getInstance();

        userBalanceTextView = findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserManager.getInstance().getCurrentUser() == null)
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

        showMarkerToggle = findViewById(R.id.show_marker_toggle);
        showMarkerToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (Marker marker : markers)
                    marker.setVisible(isChecked);
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
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    _placesManager.fetchPlaces(mMap.getMyLocation(), new PlacesManager.OnPlacesFetchedListener() {
                        @Override
                        public void onFetched(List<Place> list, SessionMError sessionMError) {
                            if (sessionMError != null) {
                                Toast.makeText(MainActivity.this, "Failed! " + sessionMError.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
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
                        }
                    });
                    return false;
                }
            });
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Place currentPlace = placesMap.get(marker.getSnippet());
                    if (currentPlace.getCheckinStatus().equals(Place.CheckinStatus.CHECKABLE))
                        _placesManager.checkIn(currentPlace, new PlacesManager.OnCheckedInListener() {
                            @Override
                            public void onCheckedIn(CheckinResult checkinResult, SessionMError sessionMError) {
                                if (sessionMError != null) {
                                    Toast.makeText(MainActivity.this, "Failed! " + sessionMError.getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Succuss! Can check in again at: " + checkinResult.getCanCheckinAgainAt(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    else
                        Toast.makeText(MainActivity.this, "Unable to check in! " + currentPlace.getCheckinStatus(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
