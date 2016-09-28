/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionM;
import com.sessionm.api.place.data.Place;
import com.sessionm.mmc.R;
import com.sessionm.mmc.view.PlacesFragment;

import java.util.List;

//Adapter class to draw Transaction List
public class PlacesRecAdapter extends RecyclerView.Adapter<PlacesRecAdapter.PlacesViewHolder> {

    private PlacesFragment _fragment;
    private final List<Place> _places;

    public PlacesRecAdapter(PlacesFragment fragment, List<Place> places) {
        _fragment = fragment;
        _places = places;
    }

    @Override
    public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.feed_item_place, parent, false);

        return new PlacesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlacesViewHolder holder, int position) {
        final Place place = _places.get(position);
        holder.idTextView.setText("ID: " + place.getID());
        holder.nameTextView.setText("Name: " + place.getName());
        holder.statusTextView.setText("Status: " + place.getCheckinStatus());
        holder.pointsTextView.setText("Points: " + place.getPoints());
        holder.addressTextView.setText("Address: " + place.getFullAddress());
        holder.distanceTextView.setText("Distance: " + place.getDistanceLabel());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (place.getCheckinStatus().equals(Place.CheckinStatus.CHECKABLE)) {
                    SessionM.getInstance().getPlacesManager().checkIn(place);
                } else {
                    Toast.makeText(_fragment.getActivity(), "Cannot checkin at this moment!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _places.size();
    }

    public static class PlacesViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView;
        TextView nameTextView;
        TextView statusTextView;
        TextView pointsTextView;
        TextView addressTextView;
        TextView distanceTextView;

        public PlacesViewHolder(View v) {
            super(v);
            idTextView = (TextView) v.findViewById(R.id.place_id);
            nameTextView = (TextView) v.findViewById(R.id.place_name);
            statusTextView = (TextView) v.findViewById(R.id.place_status);
            pointsTextView = (TextView) v.findViewById(R.id.place_points);
            addressTextView = (TextView) v.findViewById(R.id.place_address);
            distanceTextView = (TextView) v.findViewById(R.id.place_distance);
        }
    }
}