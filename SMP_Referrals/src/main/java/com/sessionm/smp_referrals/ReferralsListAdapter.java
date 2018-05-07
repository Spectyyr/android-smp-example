/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_referrals;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sessionm.referral.api.data.Referral;

import java.util.List;

//Adapter class to draw Transaction List
public class ReferralsListAdapter extends RecyclerView.Adapter<ReferralsListAdapter.ReferralsViewHolder> {

    private final List<Referral> _referrals;

    public ReferralsListAdapter(List<Referral> referrals) {
        _referrals = referrals;
    }

    @Override
    public ReferralsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.feed_item_referral, parent, false);

        return new ReferralsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReferralsViewHolder holder, int position) {
        final Referral referral = _referrals.get(position);
        holder.idTextView.setText("Referral ID: " + referral.getID());
        holder.textView_status.setText("Status: " + referral.getStatus());
        holder.textView_pending_time.setText("Pending Time: " + referral.getPendingTime());
        holder.textView_referee.setText("Referee: " + referral.getReferee());
        holder.textView_email.setText("Email: " + referral.getEmail());
        holder.textView_number.setText("Phone Number: " + referral.getPhoneNumber());
        holder.textView_origin.setText("Origin: " + referral.getOrigin());
        holder.textView_source.setText("Source: " + referral.getSource());
        holder.textView_client_data.setText("Client Data: " + referral.getClientData());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _referrals.size();
    }

    public static class ReferralsViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView;
        TextView textView_status;
        TextView textView_pending_time;
        TextView textView_referee;
        TextView textView_email;
        TextView textView_number;
        TextView textView_origin;
        TextView textView_source;
        TextView textView_client_data;

        public ReferralsViewHolder(View v) {
            super(v);
            idTextView = v.findViewById(R.id.referral_id);
            textView_status = v.findViewById(R.id.referral_status);
            textView_pending_time = v.findViewById(R.id.referral_pending_time);
            textView_referee = v.findViewById(R.id.referral_referee);
            textView_email = v.findViewById(R.id.referral_email);
            textView_number = v.findViewById(R.id.referral_number);
            textView_origin = v.findViewById(R.id.referral_origin);
            textView_source = v.findViewById(R.id.referral_source);
            textView_client_data = v.findViewById(R.id.referral_client_data);
        }
    }
}