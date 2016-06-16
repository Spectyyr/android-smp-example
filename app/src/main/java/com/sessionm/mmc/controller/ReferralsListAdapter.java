/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sessionm.api.referral.data.Referral;
import com.sessionm.mmc.R;

import java.util.List;

//Adapter class to draw Transaction List
public class ReferralsListAdapter extends BaseAdapter {

    private final Context _context;
    private final List<Referral> _referrals;
    private LayoutInflater _inflater;

    public ReferralsListAdapter(Context context, List<Referral> referrals) {
        _context = context;
        _referrals = referrals;
    }

    @Override
    public int getCount() {
        return _referrals.size();
    }

    @Override
    public Object getItem(int position) {
        return _referrals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            if (_inflater == null)
                _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = _inflater.inflate(R.layout.referral_row, parent, false);
            holder = new ViewHolder();
            holder.textView_id = (TextView) convertView.findViewById(R.id.referral_id);
            holder.textView_status = (TextView) convertView.findViewById(R.id.referral_status);
            holder.textView_pending_time = (TextView) convertView.findViewById(R.id.referral_pending_time);
            holder.textView_referee = (TextView) convertView.findViewById(R.id.referral_referee);
            holder.textView_email = (TextView) convertView.findViewById(R.id.referral_email);
            holder.textView_number = (TextView) convertView.findViewById(R.id.referral_number);
            holder.textView_origin = (TextView) convertView.findViewById(R.id.referral_origin);
            holder.textView_source = (TextView) convertView.findViewById(R.id.referral_source);
            holder.textView_client_data = (TextView) convertView.findViewById(R.id.referral_client_data);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Referral referral = _referrals.get(position);
        holder.textView_id.setText("Referral ID: " + referral.getID());
        holder.textView_status.setText("Status: " + referral.getStatus());
        holder.textView_pending_time.setText("Pending Time: " + referral.getPendingTime());
        holder.textView_referee.setText("Referee: " + referral.getReferee());
        holder.textView_email.setText("Email: " + referral.getEmail());
        holder.textView_number.setText("Phone Number: " + referral.getPhoneNumber());
        holder.textView_origin.setText("Origin: " + referral.getOrigin());
        holder.textView_source.setText("Source: " + referral.getSource());
        holder.textView_client_data.setText("Client Data: " + referral.getClientData());

        return convertView;
    }

    private static class ViewHolder {
        TextView textView_id;
        TextView textView_status;
        TextView textView_pending_time;
        TextView textView_referee;
        TextView textView_email;
        TextView textView_number;
        TextView textView_origin;
        TextView textView_source;
        TextView textView_client_data;
    }
}