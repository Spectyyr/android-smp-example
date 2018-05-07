/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.instantapp.feature;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.sessionm.api.SessionM;
import com.sessionm.api.campaign.data.FeedMessage;
import com.squareup.picasso.Picasso;

import java.util.List;

//Adapter class to draw the Promotions Message List and handle Feed Message events
public class CampaignsRecAdapter extends RecyclerView.Adapter<CampaignsRecAdapter.CampaignsViewHolder> {

    private CampaignsFragment _fragment;
    private List<FeedMessage> _messages;

    public CampaignsRecAdapter(CampaignsFragment fragment, List<FeedMessage> messages) {
        _fragment = fragment;
        _messages = messages;
    }

    @Override
    public CampaignsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.feed_item_campaign, parent, false);

        return new CampaignsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CampaignsViewHolder holder, int position) {
        final FeedMessage item = _messages.get(position);

        //Returns the message header
        holder.headerTextView.setText(item.getHeader());

        //Returns the message sub header
        holder.subHeaderTextView.setText(item.getSubHeader());

        //Returns the message period
        holder.periodTextView.setText(item.getStartTime() + " - " + item.getEndTime());

        //There is no need to draw the description if it was not set
        if (!TextUtils.isEmpty(item.getDescription())) {
            //Returns the Message description, String
            holder.descriptionTextView.setText(item.getDescription());
            holder.descriptionTextView.setVisibility(View.VISIBLE);
        } else {
            holder.descriptionTextView.setVisibility(View.GONE);
        }

        //TODO: set value, might be points
        int points = item.getPoints();
        if (points == 0)
            holder.valueTextView.setVisibility(View.GONE);
        else
            holder.valueTextView.setText(points + " pts");

        //Any customized value in data field
        /*JSONObject data = item.getData();
        if (data != null) {
            String value = data.optString("value");
            valueTextView.setText(value);
        }*/

        //There is no need to draw the image if there is not icon URL
        if (item.getIconURL() != null && !item.getIconURL().equals("null")) {
            //Returns the Message image URL, String
            Picasso.with(_fragment.getActivity()).load(item.getIconURL()).into(holder.iconImageView);
            holder.iconImageView.setVisibility(View.VISIBLE);
        } else {
            holder.iconImageView.setVisibility(View.GONE);
        }

        //There is no need to draw the image if there is not image URL
        String imageURL = item.getImageURL();
        if (imageURL != null && !imageURL.equals("null")) {
            if (imageURL.endsWith("mp4")) {
                final Uri videoUri = Uri.parse(imageURL);
                holder.feedImageView.setVisibility(View.GONE);
                holder.videoView.setVisibility(View.VISIBLE);
                holder.videoView.setVideoURI(videoUri);
                holder.videoView.start();
                holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                    }
                });
                holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                    }
                });
            } else {
                //Returns the Message image URL, String
                Picasso.with(_fragment.getActivity()).load(item.getImageURL()).into(holder.feedImageView);
                holder.feedImageView.setVisibility(View.VISIBLE);
                holder.videoView.setVisibility(View.GONE);
            }
        } else {
            holder.feedImageView.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.notifyTapped();
                showDetails(item);
            }
        });

        item.notifySeen();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _messages.size();
    }

    //TODO Needs to handle more events
    private void showDetails(FeedMessage data) {
        SessionM.getInstance().getCampaignsManager().executeMessageAction(data.getMessageID());
        FeedMessage.MessageActionType actionType = data.getActionType();
        if (!actionType.equals(FeedMessage.MessageActionType.FULL_SCREEN)) {
            _fragment.onItemTapped(actionType, data.getActionURL());
        }
    }

    public static class CampaignsViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView headerTextView;
        TextView subHeaderTextView;
        TextView periodTextView;
        TextView descriptionTextView;
        TextView valueTextView;
        ImageView feedImageView;
        VideoView videoView;

        public CampaignsViewHolder(View v) {
            super(v);
            iconImageView = v.findViewById(R.id.promotion_icon_image);
            headerTextView = v.findViewById(R.id.promotion_header_text);
            subHeaderTextView = v.findViewById(R.id.promotion_subheader_text);
            periodTextView = v.findViewById(R.id.promotion_period_text);
            descriptionTextView = v.findViewById(R.id.promotion_detail_text);
            valueTextView = v.findViewById(R.id.promotion_value_text);
            feedImageView = v.findViewById(R.id.promotion_main_image);
            videoView = v.findViewById(R.id.promotion_main_video);
        }
    }
}