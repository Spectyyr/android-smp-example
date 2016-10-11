/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc_rewards;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sessionm.api.reward.data.offer.Offer;
import com.squareup.picasso.Picasso;

import java.util.List;

//Adapter class to draw Rewards List and handle Offer Image events
public class RewardsRecAdapter extends RecyclerView.Adapter<RewardsRecAdapter.RewardsViewHolder> {

    private RewardsFragment _fragment;
    private List<Offer> _offers;

    public RewardsRecAdapter(RewardsFragment fragment, List<Offer> offers) {
        _fragment = fragment;
        _offers = offers;
    }

    @Override
    public RewardsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.feed_item_parent_reward, parent, false);

        return new RewardsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RewardsViewHolder holder, int position) {
        final Offer offer = _offers.get(position);

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
        Picasso.with(_fragment.getContext()).load(offer.getLogoURL()).into(holder.feedImageView);

        holder.feedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offerDetailsIntent = new Intent(_fragment.getActivity(), OfferDetailsActivity.class);
                offerDetailsIntent.putExtra("offer_id", offer.getID());
                _fragment.getActivity().startActivity(offerDetailsIntent);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _offers.size();
    }

    public static class RewardsViewHolder extends RecyclerView.ViewHolder {
        TextView headerTextView;
        TextView subHeaderTextView;
        TextView statusTextView;
        TextView descriptionTextView;
        TextView valueTextView;
        ImageView feedImageView;

        public RewardsViewHolder(View v) {
            super(v);
            headerTextView = (TextView) v.findViewById(R.id.reward_header_text);
            subHeaderTextView = (TextView) v.findViewById(R.id.reward_subheader_text);
            statusTextView = (TextView) v.findViewById(R.id.reward_status_text);
            descriptionTextView = (TextView) v.findViewById(R.id.reward_detail_text);
            valueTextView = (TextView) v.findViewById(R.id.reward_value_text);
            feedImageView = (ImageView) v.findViewById(R.id.reward_main_image);
        }
    }
}