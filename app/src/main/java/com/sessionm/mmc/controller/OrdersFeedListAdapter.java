/*
 * Copyright (c) 2015 SessionM. All rights reserved.
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

    private Activity activity;
    private LayoutInflater inflater;
    private List<Order> orders;
    private String mTAG = "RewardsFragment";

    public OrdersFeedListAdapter(Activity activity, List<Order> orders) {
        this.activity = activity;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int location) {
        return orders.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item_order, null);

        TextView idTextView = (TextView) convertView.findViewById(R.id.order_id);
        TextView nameTextView = (TextView) convertView
                .findViewById(R.id.order_name);
        TextView quantityTextView = (TextView) convertView.findViewById(R.id.order_quantity);
        TextView pointsTextView = (TextView) convertView.findViewById(R.id.order_points);
        TextView statusTextView = (TextView) convertView.findViewById(R.id.order_status);
        TextView createdAtTextView = (TextView) convertView.findViewById(R.id.order_created_time);
        TextView descriptionTextView = (TextView) convertView
                .findViewById(R.id.order_description);

        final Order order = orders.get(position);

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