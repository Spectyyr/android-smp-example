/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc_rewards;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sessionm.api.reward.data.order.Order;

import java.util.List;

public class OrdersFeedListAdapter extends RecyclerView.Adapter<OrdersFeedListAdapter.OrdersViewHolder> {

    private List<Order> _orders;

    public OrdersFeedListAdapter(OrdersFragment fragment, List<Order> orders) {
        _orders = orders;
    }

    @Override
    public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.feed_item_order, parent, false);

        return new OrdersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrdersViewHolder holder, int position) {
        final Order order = _orders.get(position);

        holder.idTextView.setText("ID: " + order.getID());
        holder.nameTextView.setText("Name: " + order.getName());
        holder.quantityTextView.setText("Quantity: " + order.getQuantity());
        holder.pointsTextView.setText("Points: " + order.getPoints());
        holder.statusTextView.setText("Status: " + order.getStatus());
        holder.createdAtTextView.setText("Created Time: " + order.getCreatedAt());
        holder.descriptionTextView.setText("Description: " + order.getDescription());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _orders.size();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView;
        TextView nameTextView;
        TextView quantityTextView;
        TextView pointsTextView;
        TextView statusTextView;
        TextView createdAtTextView;
        TextView descriptionTextView;

        public OrdersViewHolder(View v) {
            super(v);
            idTextView = (TextView) v.findViewById(R.id.order_id);
            nameTextView = (TextView) v.findViewById(R.id.order_name);
            quantityTextView = (TextView) v.findViewById(R.id.order_quantity);
            pointsTextView = (TextView) v.findViewById(R.id.order_points);
            statusTextView = (TextView) v.findViewById(R.id.order_status);
            createdAtTextView = (TextView) v.findViewById(R.id.order_created_time);
            descriptionTextView = (TextView) v.findViewById(R.id.order_description);
        }
    }
}