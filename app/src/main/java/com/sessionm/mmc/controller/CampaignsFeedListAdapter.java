/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.controller;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sessionm.api.SessionM;
import com.sessionm.api.message.data.Message;
import com.sessionm.api.message.feed.data.FeedMessage;
import com.sessionm.mmc.R;
import com.squareup.picasso.Picasso;

import java.util.List;

//Adapter class to draw the Promotions Message List and handle Feed Message events
public class CampaignsFeedListAdapter extends BaseAdapter {

    private Activity _activity;
    private LayoutInflater _inflater;
    private List<FeedMessage> _messages;

    public CampaignsFeedListAdapter(Activity activity, List<FeedMessage> messages) {
        _activity = activity;
        _messages = messages;
    }

    @Override
    public int getCount() {
        return _messages.size();
    }

    @Override
    public Object getItem(int location) {
        return _messages.get(location);
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
            convertView = _inflater.inflate(R.layout.feed_item_campaign, null);

        ImageView iconImageView = (ImageView) convertView
                .findViewById(R.id.promotion_icon_image);

        TextView headerTextView = (TextView) convertView.findViewById(R.id.promotion_header_text);
        TextView subHeaderTextView = (TextView) convertView
                .findViewById(R.id.promotion_subheader_text);
        TextView periodTextView = (TextView) convertView
                .findViewById(R.id.promotion_period_text);
        TextView descriptionTextView = (TextView) convertView
                .findViewById(R.id.promotion_detail_text);
        TextView valueTextView = (TextView) convertView
                .findViewById(R.id.promotion_value_text);

        ImageView feedImageView = (ImageView) convertView
                .findViewById(R.id.promotion_main_image);

        final FeedMessage item = _messages.get(position);

        //Returns the message header
        headerTextView.setText(item.getHeader());

        //Returns the message sub header
        subHeaderTextView.setText(item.getSubHeader());

        //Returns the message period
        periodTextView.setText(item.getStartTime() + " - " + item.getEndTime());

        //There is no need to draw the description if it was not set
        if (!TextUtils.isEmpty(item.getDescription())) {
            //Returns the Message description, String
            descriptionTextView.setText(item.getDescription());
            descriptionTextView.setVisibility(View.VISIBLE);
        } else {
            descriptionTextView.setVisibility(View.GONE);
        }

        //TODO: set value, might be points
        valueTextView.setText("10");

        //Any customized value in data field
        /*JSONObject data = item.getData();
        if (data != null) {
            String value = data.optString("value");
            valueTextView.setText(value);
        }*/

        //There is no need to draw the image if there is not icon URL
        if (item.getIconURL() != null && !item.getIconURL().equals("null")) {
            //Returns the Message image URL, String
            Picasso.with(_activity).load(item.getIconURL()).into(iconImageView);
            iconImageView.setVisibility(View.VISIBLE);
        } else {
            iconImageView.setVisibility(View.GONE);
        }

        //There is no need to draw the image if there is not image URL
        if (item.getImageURL() != null && !item.getImageURL().equals("null")) {
            //Returns the Message image URL, String
            Picasso.with(_activity).load(item.getImageURL()).into(feedImageView);
            feedImageView.setVisibility(View.VISIBLE);
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.notifyTapped();
                showDetails(item);
            }
        });

        item.notifySeen();
        return convertView;
    }

    //TODO
    private void showDetails(FeedMessage data){
        if (data.getActionType().equals(Message.MessageActionType.FULL_SCREEN))
            SessionM.getInstance().presentActivity(SessionM.ActivityType.PORTAL, data.getActionURL());
    }
}