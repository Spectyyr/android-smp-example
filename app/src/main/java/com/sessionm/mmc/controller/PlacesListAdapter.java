/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sessionm.api.place.data.Place;
import com.sessionm.api.receipt.data.Receipt;
import com.sessionm.api.receipt.data.ReceiptResult;
import com.sessionm.mmc.R;

import java.util.List;

//Adapter class to draw Transaction List
public class PlacesListAdapter extends BaseAdapter {

    private final Context _context;
    private final List<Place> _places;
    private LayoutInflater _inflater;

    public PlacesListAdapter(Context context, List<Place> places) {
        _context = context;
        _places = places;
    }

    @Override
    public int getCount() {
        return _places.size();
    }

    @Override
    public Object getItem(int position) {
        return _places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            if (_inflater == null)
                _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = _inflater.inflate(R.layout.place_row, parent, false);
            holder = new ViewHolder();
            holder.textViewID = (TextView) convertView.findViewById(R.id.place_id);
            holder.textViewName = (TextView) convertView.findViewById(R.id.place_name);
            holder.textViewStatus = (TextView) convertView.findViewById(R.id.place_status);
            holder.textViewPoints = (TextView) convertView.findViewById(R.id.place_points);
            holder.textViewAddress = (TextView) convertView.findViewById(R.id.place_address);
            holder.textViewDistance = (TextView) convertView.findViewById(R.id.place_distance);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (_places != null && _places.size() > 0) {
            Place place = _places.get(position);
            holder.textViewID.setText("ID: " + place.getID());
            holder.textViewName.setText("Name: " + place.getName());
            holder.textViewStatus.setText("Status: " + place.getCheckinStatus());
            holder.textViewPoints.setText("Points: " + place.getPoints());
            holder.textViewAddress.setText("Address: " + place.getFullAddress());
            holder.textViewDistance.setText("Distance: " + place.getDistanceLabel());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView textViewID;
        TextView textViewName;
        TextView textViewStatus;
        TextView textViewPoints;
        TextView textViewAddress;
        TextView textViewDistance;
    }
}