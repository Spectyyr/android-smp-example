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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.place.PlacesListener;
import com.sessionm.api.place.PlacesManager;
import com.sessionm.api.place.data.CheckinResult;
import com.sessionm.api.place.data.Place;
import com.sessionm.mmc.R;
import com.sessionm.mmc.controller.PlacesRecAdapter;
import com.sessionm.mmc.util.LocationObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PlacesFragment extends BaseScrollAndRefreshFragment implements Observer {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private PlacesRecAdapter _placesRecAdapter;
    List<Place> _places;
    Location _lastLocation;
    private RecyclerView _recyclerView;

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
        _placesManager.setListener(_placeListener);
        _places = new ArrayList<>(_placesManager.getPlaces());

        _recyclerView = (RecyclerView) rootView.findViewById(R.id.places_list);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _recyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        _placesRecAdapter = new PlacesRecAdapter(this, _places);
        _recyclerView.setAdapter(_placesRecAdapter);

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
            _placesRecAdapter.notifyDataSetChanged();
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
