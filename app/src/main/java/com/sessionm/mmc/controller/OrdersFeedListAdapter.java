/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sessionm.api.reward.data.order.Order;
import com.sessionm.mmc.R;

import java.util.List;

//Adapter class to draw Rewards List and handle Offer Image events
public class OrdersFeedListAdapter extends BaseAdapter {

    private Activity _activity;
    private LayoutInflater _inflater;
    private List<Order> _orders;

    public OrdersFeedListAdapter(Activity activity, List<Order> orders) {
        _activity = activity;
        _orders = orders;
    }

    @Override
    public int getCount() {
        return _orders.size();
    }

    @Override
    public Object getItem(int location) {
        return _orders.get(location);
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
                _inflater = (LayoutInflater) _activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = _inflater.inflate(R.layout.feed_item_order, null);
            holder = new ViewHolder();
            holder.idTextView = (TextView) convertView.findViewById(R.id.order_id);
            holder.nameTextView = (TextView) convertView
                    .findViewById(R.id.order_name);
            holder.quantityTextView = (TextView) convertView.findViewById(R.id.order_quantity);
            holder.pointsTextView = (TextView) convertView.findViewById(R.id.order_points);
            holder.statusTextView = (TextView) convertView.findViewById(R.id.order_status);
            holder.createdAtTextView = (TextView) convertView.findViewById(R.id.order_created_time);
            holder.descriptionTextView = (TextView) convertView
                    .findViewById(R.id.order_description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final Order order = _orders.get(position);

        holder.idTextView.setText("ID: " + order.getID());
        holder.nameTextView.setText("Name: " + order.getName());
        holder.quantityTextView.setText("Quantity: " + order.getQuantity());
        holder.pointsTextView.setText("Points: " + order.getPoints());
        holder.statusTextView.setText("Status: " + order.getStatus());
        holder.createdAtTextView.setText("Created Time: " + order.getCreatedAt());
        holder.descriptionTextView.setText("Description: " + order.getDescription());

        return convertView;
    }

    private static class ViewHolder {
        TextView idTextView;
        TextView nameTextView;
        TextView quantityTextView;
        TextView pointsTextView;
        TextView statusTextView;
        TextView createdAtTextView;
        TextView descriptionTextView;
    }
}