/*
* Copyright (c) 2016 SessionM. All rights reserved.
*/

package com.sessionm.mmc.view;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.place.PlacesListener;
import com.sessionm.api.place.PlacesManager;
import com.sessionm.api.place.data.CheckinResult;
import com.sessionm.api.place.data.Place;
import com.sessionm.mmc.R;
import com.sessionm.mmc.controller.PlacesListAdapter;
import com.sessionm.mmc.util.LocationObserver;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PlacesFragment extends BaseScrollAndRefreshFragment implements Observer {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private ObservableListView _listView;
    private PlacesListAdapter _listAdapter;
    List<Place> _places;
    Location _lastLocation;

    PlacesManager _placesManager = SessionM.getInstance().getPlacesManager();

    public static PlacesFragment newInstance() {
        PlacesFragment f = new PlacesFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _listView = (ObservableListView) rootView.findViewById(R.id.places_list);
        _places = new ArrayList<>(_placesManager.getPlaces());
        _listAdapter = new PlacesListAdapter(getActivity(), _places);
        _listView.setAdapter(_listAdapter);

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = _places.get(position);
                if (place.getCheckinStatus().equals(Place.CheckinStatus.CHECKABLE)) {
                    _placesManager.checkIn(place);
                } else {
                    Toast.makeText(getActivity(), "Cannot checkin at this moment!", Toast.LENGTH_LONG).show();
                }
            }
        });

        _listView.setScrollViewCallbacks(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    PlacesListener _placeListener = new PlacesListener() {
        @Override
        public void onPlacesFetched(List<Place> places) {
            _swipeRefreshLayout.setRefreshing(false);
            if (PlacesFragment.this._places == null) {
                PlacesFragment.this._places = new ArrayList<>();
            } else {
                PlacesFragment.this._places.clear();
            }
            PlacesFragment.this._places.addAll(places);
            if (_listAdapter == null) {
                _listAdapter = new PlacesListAdapter(getActivity(), PlacesFragment.this._places);
                _listView.setAdapter(_listAdapter);
            }
            _listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCheckedIn(CheckinResult checkinResult) {
            popUpCheckinResultDialog(checkinResult, null);
        }

        @Override
        public void onFailure(SessionMError error) {
            _swipeRefreshLayout.setRefreshing(false);
            popUpCheckinResultDialog(null, error.getMessage());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        _placesManager.setListener(_placeListener);
        LocationObserver.getInstance(getActivity()).addObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationObserver.getInstance(getActivity()).deleteObserver(this);
    }

    @Override
    public void onRefresh() {
        fetchPlaces(null);
    }

    public void fetchPlaces(String adUnitID) {
        if (_lastLocation != null) {
            _placesManager.fetchPlaces(_lastLocation, 1500, 100, adUnitID);
        } else {
            Toast.makeText(getActivity(), "Location is null!", Toast.LENGTH_SHORT).show();
            _swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        _lastLocation = (Location) data;
    }

    protected void popUpCheckinResultDialog(final CheckinResult checkinResult, String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_checkin, null);
        TextView statusTextView = (TextView) dialogLayout.findViewById(R.id.checkin_result_status);
        final TextView bonusTextView = (TextView) dialogLayout.findViewById(R.id.checkin_result_bonus);

        if (checkinResult == null) {
            statusTextView.setText("Failed!");
            bonusTextView.setText(errorMessage);
        } else {
            statusTextView.setText("Success!");
            if (checkinResult.getMessage() == null)
                bonusTextView.setText("Dismiss");
            bonusTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkinResult.getMessage() != null) {
                        String actionURL = checkinResult.getMessage().getActionURL();
                        if (actionURL != null)
                            SessionM.getInstance().presentActivity(SessionM.ActivityType.PORTAL, actionURL);
                    }
                    dialog.dismiss();
                }
            });
        }

        dialog.setView(dialogLayout);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        dialog.show();
    }
}
