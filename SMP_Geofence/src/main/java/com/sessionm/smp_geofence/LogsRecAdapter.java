/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_geofence;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

//Adapter class to draw Rewards List and handle Offer Image events
public class LogsRecAdapter extends RecyclerView.Adapter<LogsRecAdapter.LogsViewHolder> {

    private LogsFragment _fragment;
    private List<GeofenceLog> _logs;

    public LogsRecAdapter(LogsFragment fragment, List<GeofenceLog> logs) {
        _fragment = fragment;
        _logs = logs;
    }

    @Override
    public LogsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.feed_item_log, parent, false);

        return new LogsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LogsViewHolder holder, int position) {
        final GeofenceLog geofenceLog = _logs.get(position);

        holder.timestamp.setText(geofenceLog.getDisplayTime());
        holder.name.setText(geofenceLog.getName());
        holder.message.setText(geofenceLog.getMessage());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _logs.size();
    }

    public static class LogsViewHolder extends RecyclerView.ViewHolder {
        TextView timestamp;
        TextView name;
        TextView message;

        public LogsViewHolder(View v) {
            super(v);
            timestamp = v.findViewById(R.id.timestamp);
            name = v.findViewById(R.id.name);
            message = v.findViewById(R.id.message);
        }
    }
}
