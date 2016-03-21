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

        if (_inflater == null)
            _inflater = (LayoutInflater) _activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = _inflater.inflate(R.layout.feed_item_order, null);

        TextView idTextView = (TextView) convertView.findViewById(R.id.order_id);
        TextView nameTextView = (TextView) convertView
                .findViewById(R.id.order_name);
        TextView quantityTextView = (TextView) convertView.findViewById(R.id.order_quantity);
        TextView pointsTextView = (TextView) convertView.findViewById(R.id.order_points);
        TextView statusTextView = (TextView) convertView.findViewById(R.id.order_status);
        TextView createdAtTextView = (TextView) convertView.findViewById(R.id.order_created_time);
        TextView descriptionTextView = (TextView) convertView
                .findViewById(R.id.order_description);

        final Order order = _orders.get(position);

        idTextView.setText("ID: " + order.getID());
        nameTextView.setText("Name: " + order.getName());
        quantityTextView.setText("Quantity: " + order.getQuantity());
        pointsTextView.setText("Points: " + order.getPoints());
        statusTextView.setText("Status: " + order.getStatus());
        createdAtTextView.setText("Created Time: " + order.getCreatedAt());
        descriptionTextView.setText("Description: " + order.getDescription());

        return convertView;
    }
}