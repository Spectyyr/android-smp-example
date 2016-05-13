/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sessionm.api.loyaltycard.data.LoyaltyCard;
import com.sessionm.mmc.R;
import com.squareup.picasso.Picasso;

import java.util.List;

//Adapter class to draw Rewards List and handle Offer Image events
public class LoyaltyCardsListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<LoyaltyCard> cards;
    private String TAG = "RewardsFragment";

    public LoyaltyCardsListAdapter(Activity activity, List<LoyaltyCard> cards) {
        this.activity = activity;
        this.cards = cards;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int location) {
        return cards.get(location);
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

            convertView = inflater.inflate(R.layout.card_list_row, null);

            holder = new ViewHolder();
            holder.cardNumber = (TextView) convertView.findViewById(R.id.card_number);
            holder.retailerName = (TextView) convertView.findViewById(R.id.retailer_name);
            holder.cardID = (TextView) convertView.findViewById(R.id.id);
            holder.icon = (ImageView) convertView.findViewById(R.id.retailer_icon);
            holder.linked = (CheckBox) convertView.findViewById(R.id.linked_chb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LoyaltyCard card = cards.get(position);

        holder.cardNumber.setText(card.getCardNumber());
        holder.cardID.setText(card.getID());
        holder.retailerName.setText(card.getRetailer().getName());
        if ((card.getRetailer() != null) && (card.getRetailer().getIcon() != null)) {
            Picasso.with(activity).load(card.getRetailer().getIcon()).into(holder.icon);
        }
        holder.linked.setChecked(card.getLinked());

        return convertView;
    }

    private static class ViewHolder {
        TextView cardNumber;
        TextView retailerName;
        TextView cardID;
        ImageView icon;
        CheckBox linked;
    }
}
