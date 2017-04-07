/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_contents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sessionm.api.content.data.Content;
import com.squareup.picasso.Picasso;

import java.util.List;

//Adapter class to draw the Promotions Message List and handle Feed Message events
public class ContentsRecAdapter extends RecyclerView.Adapter<ContentsRecAdapter.CampaignsViewHolder> {

    private ContentsFragment _fragment;
    private List<Content> _contents;

    public ContentsRecAdapter(ContentsFragment fragment, List<Content> contents) {
        _fragment = fragment;
        _contents = contents;
    }

    @Override
    public CampaignsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.feed_item_content, parent, false);

        return new CampaignsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CampaignsViewHolder holder, int position) {
        final Content item = _contents.get(position);

        //Returns the message header
        holder.headerTextView.setText(item.getName());

        //Returns the message sub header
        holder.subHeaderTextView.setText(item.getCreatedTime());

        //Returns the message period.
        //holder.periodTextView.setText(item.getUpdatedTime() + " - " + item.getExpiresTime());

        //There is no need to draw the description if it was not set
        if (!TextUtils.isEmpty(item.getDescription())) {
            //Returns the Message description, String
            holder.descriptionTextView.setText(item.getDescription());
            holder.descriptionTextView.setVisibility(View.VISIBLE);
        } else {
            holder.descriptionTextView.setVisibility(View.GONE);
        }

        int weight = item.getWeight();
        if (weight == 0)
            holder.valueTextView.setVisibility(View.GONE);
        else
            holder.valueTextView.setText(weight + "");

        //There is no need to draw the image if there is not image URL
        String imageURL = item.getImageURL();
        if (imageURL != null && !imageURL.equals("null")) {
            //Returns the Message image URL, String
            Picasso.with(_fragment.getActivity()).load(item.getImageURL()).into(holder.feedImageView);
            holder.feedImageView.setVisibility(View.VISIBLE);
        } else {
            holder.feedImageView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putString("content_id", item.getID());
                Intent intent = new Intent(_fragment.getActivity(), ContentActivity.class);
                intent.putExtras(extras);
                _fragment.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _contents.size();
    }

    public static class CampaignsViewHolder extends RecyclerView.ViewHolder {
        TextView headerTextView;
        TextView subHeaderTextView;
        TextView periodTextView;
        TextView descriptionTextView;
        TextView valueTextView;
        ImageView feedImageView;

        public CampaignsViewHolder(View v) {
            super(v);
            headerTextView = (TextView) v.findViewById(R.id.content_header_text);
            subHeaderTextView = (TextView) v.findViewById(R.id.content_subheader_text);
            periodTextView = (TextView) v.findViewById(R.id.content_period_text);
            descriptionTextView = (TextView) v.findViewById(R.id.content_detail_text);
            valueTextView = (TextView) v.findViewById(R.id.content_value_text);
            feedImageView = (ImageView) v.findViewById(R.id.content_main_image);
        }
    }
}