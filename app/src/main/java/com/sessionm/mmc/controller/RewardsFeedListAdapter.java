/*
 * Copyright (c) 2015 SessionM. All rights reserved.
 */

package com.sessionm.mmc.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sessionm.api.reward.data.offer.Offer;
import com.sessionm.mmc.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

//Adapter class to draw Rewards List and handle Offer Image events
public class RewardsFeedListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Offer> offers;
    private List<Row>rows = new ArrayList<Row>();
    private String mTAG = "RewardsFragment";

    public RewardsFeedListAdapter(Activity activity, List<Offer> offers) {
        this.activity = activity;
        this.offers = offers;
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public int getItemViewType(int position) {
        Row r = rows.get(position);
        return r._hier.equals(TYPES.CHILD) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    enum TYPES { PARENT, CHILD };

    @Override
    public void notifyDataSetChanged() {
        for (Offer o : this.offers) {
            rows.add(new Row(TYPES.PARENT, o));
            for (Offer oo : o.getOptions()) {
                Log.d("TAG", String.format("[%s][%s]", o.getName(), oo.getName()));
                rows.add(new Row(TYPES.CHILD, o));
            }
        }
        super.notifyDataSetChanged();
    }

    public static class Row {

        public final Offer _offer;
        private final TYPES _hier;

        public Row(TYPES hier, Offer offer) {
            _hier = hier;
            _offer = offer;
        }
    }

    @Override
    public Object getItem(int location) {
        return rows.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Row row = rows.get(position);
        final Offer offer = row._offer;

        if (row._hier.equals(TYPES.CHILD)) {
            convertView = inflater.inflate(R.layout.feed_item_child_reward, null);
            TextView headerTextView = (TextView)convertView.findViewById(R.id.reward_child_header_text);
            TextView valueTextView = (TextView)convertView.findViewById(R.id.reward_child_value_text);
            headerTextView.setText(offer.getName());
            valueTextView.setText("" + offer.getPoints());
        } else {
            convertView = inflater.inflate(R.layout.feed_item_parent_reward, null);

            TextView headerTextView = (TextView) convertView.findViewById(R.id.reward_header_text);
            TextView subHeaderTextView = (TextView) convertView.findViewById(R.id.reward_subheader_text);
            TextView descriptionTextView = (TextView) convertView.findViewById(R.id.reward_detail_text);
            TextView valueTextView = (TextView) convertView.findViewById(R.id.reward_value_text);
            ImageView feedImageView = (ImageView) convertView.findViewById(R.id.reward_main_image);

            if (offer.getOptions().size() > 0) {
                Log.d("TAG", "Size: " + offer.getOptions().size());
            }

            headerTextView.setText(offer.getName());
            String startTime = offer.getStartTime();
            String endTime = offer.getEndTime();
            String out = String.format("%s - %s", startTime != null ? startTime : "", endTime != null ? endTime : "");
            subHeaderTextView.setText(out);
            descriptionTextView.setText(offer.getDescription() != null ? offer.getDescription() : "");
            valueTextView.setText("" + offer.getPoints());
            Picasso.with(activity).load(offer.getLogo()).into(feedImageView);
        }
        return convertView;
    }

}