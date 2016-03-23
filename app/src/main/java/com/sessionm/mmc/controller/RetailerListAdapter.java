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

import java.util.List;

//Adapter class to draw Rewards List and handle Offer Image events
public class RetailerListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Retailer> retailers;
    private String mTAG = "LoyaltyFragment";

    public RetailerListAdapter(Activity activity, List<Retailer> cards) {
        this.activity = activity;
        this.retailers = cards;
    }

    @Override
    public int getCount() {
        return retailers.size();
    }

    @Override
    public void notifyDataSetChanged() {
//                rows.add(new Row(TYPES.CHILD, o));
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int location) {
        return retailers.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Retailer row = retailers.get(position);

        convertView = inflater.inflate(R.layout.retailer_list_row, null);
        TextView name = (TextView)convertView.findViewById(R.id.retailer);
        TextView card = (TextView)convertView.findViewById(R.id.card);

        ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
        ImageView image = (ImageView)convertView.findViewById(R.id.image);

        name.setText(row.getName());
        card.setText(row.getCard());

        String iconURL = row.getIcon();
        if (iconURL != null && !iconURL.equals("null")) {
            Picasso.with(activity).load(iconURL).into(icon);
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }

        return convertView;
    }

}