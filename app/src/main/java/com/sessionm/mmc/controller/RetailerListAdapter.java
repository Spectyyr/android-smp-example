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
import android.widget.ImageView;
import android.widget.TextView;

import com.sessionm.api.loyaltycard.data.Retailer;
import com.sessionm.mmc.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

//Adapter class to draw Rewards List and handle Offer Image events
public class RetailerListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Retailer> _retailer;
    private List<Retailer> _searched;
    private String mTAG = "LoyaltyFragment";

    public RetailerListAdapter(Activity activity, List<Retailer> cards) {
        this.activity = activity;
        setRetailers(cards);
    }

    @Override
    public int getCount() {
        return _searched.size();
    }

    public void setRetailers(List<Retailer> retailers) {
        _retailer = retailers;
        _searched = new ArrayList(retailers);
    }

    @Override
    public Object getItem(int location) {
        return _searched.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            if (inflater == null)
                inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.feed_item_retailer, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.retailer);
            holder.card = (TextView) convertView.findViewById(R.id.card);
            holder.icon = (ImageView) convertView.findViewById(R.id.retailer_icon);
            holder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Retailer row = _searched.get(position);

        holder.name.setText(row.getName());
        holder.card.setText(row.getCard());

        String iconURL = row.getIconURL();
        if (iconURL != null && !iconURL.equals("null")) {
            Picasso.with(activity).load(iconURL).into(holder.icon);
            holder.icon.setVisibility(View.VISIBLE);
        } else {
            holder.icon.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void filter(String s) {
        _searched.clear();
        for (Retailer retailer : _retailer) {
            if (retailer.getName().toLowerCase().contains(s.toLowerCase()) || retailer.getCard().toLowerCase().contains(s.toLowerCase())) {
                _searched.add(retailer);
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView name;
        TextView card;
        ImageView icon;
        ImageView image;
    }
}