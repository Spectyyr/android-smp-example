/*
 * Copyright (c) 2016 SessionM. All rights reserved.
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

    private Activity _activity;
    private LayoutInflater _inflater;
    private List<Offer> _offers;
    private List<Row> _rows = new ArrayList<>();

    public RewardsFeedListAdapter(Activity activity, List<Offer> offers) {
        _activity = activity;
        _offers = offers;
    }

    @Override
    public int getCount() {
        return _rows.size();
    }

    @Override
    public int getItemViewType(int position) {
        Row r = _rows.get(position);
        return r._tier.equals(TYPES.CHILD) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    enum TYPES {PARENT, CHILD}

    @Override
    public void notifyDataSetChanged() {
        for (Offer o : this._offers) {
            _rows.add(new Row(TYPES.PARENT, o));
            for (Offer oo : o.getOptions()) {
                _rows.add(new Row(TYPES.CHILD, o));
            }
        }
        super.notifyDataSetChanged();
    }

    public static class Row {

        public final Offer _offer;
        private final TYPES _tier;

        public Row(TYPES tier, Offer offer) {
            _tier = tier;
            _offer = offer;
        }
    }

    @Override
    public Object getItem(int location) {
        return _rows.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (_inflater == null)
            _inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Row row = _rows.get(position);
        final Offer offer = row._offer;

        if (row._tier.equals(TYPES.CHILD)) {
            convertView = _inflater.inflate(R.layout.feed_item_child_reward, null);
            TextView headerTextView = (TextView) convertView.findViewById(R.id.reward_child_header_text);
            TextView valueTextView = (TextView) convertView.findViewById(R.id.reward_child_value_text);
            headerTextView.setText(offer.getName());
            valueTextView.setText("" + offer.getPoints());
        } else {
            if (convertView == null) {
                convertView = _inflater.inflate(R.layout.feed_item_parent_reward, null);
                holder = new ViewHolder();
                holder.headerTextView = (TextView) convertView.findViewById(R.id.reward_header_text);
                holder.subHeaderTextView = (TextView) convertView.findViewById(R.id.reward_subheader_text);
                holder.statusTextView = (TextView) convertView.findViewById(R.id.reward_status_text);
                holder.descriptionTextView = (TextView) convertView.findViewById(R.id.reward_detail_text);
                holder.valueTextView = (TextView) convertView.findViewById(R.id.reward_value_text);
                holder.feedImageView = (ImageView) convertView.findViewById(R.id.reward_main_image);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (offer.getOptions().size() > 0) {
                Log.d("TAG", "Size: " + offer.getOptions().size());
            }

            holder.headerTextView.setText(offer.getName());
            String startTime = offer.getStartTime();
            String endTime = offer.getEndTime();
            String out = String.format("%s - %s", startTime != null ? startTime : "", endTime != null ? endTime : "");
            holder.subHeaderTextView.setText(out);
            holder.statusTextView.setText(offer.getStatus().toString());
            holder.descriptionTextView.setText(offer.getDescription() != null ? offer.getDescription() : "");
            holder.valueTextView.setText("" + offer.getPoints());
            Picasso.with(_activity).load(offer.getLogo()).into(holder.feedImageView);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView headerTextView;
        TextView subHeaderTextView;
        TextView statusTextView;
        TextView descriptionTextView;
        TextView valueTextView;
        ImageView feedImageView;
    }
}