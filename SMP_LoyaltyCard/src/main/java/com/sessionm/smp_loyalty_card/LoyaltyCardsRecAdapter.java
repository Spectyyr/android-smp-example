/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_loyalty_card;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.core.api.SessionMError;
import com.sessionm.loyaltycard.api.LoyaltyCardsManager;
import com.sessionm.loyaltycard.api.data.LoyaltyCard;
import com.squareup.picasso.Picasso;

import java.util.List;

//Adapter class to draw Rewards List and handle Offer Image events
public class LoyaltyCardsRecAdapter extends RecyclerView.Adapter<LoyaltyCardsRecAdapter.LoyaltyCardsViewHolder> {

    private LoyaltyFragment _fragment;
    private List<LoyaltyCard> _cards;

    public LoyaltyCardsRecAdapter(LoyaltyFragment fragment, List<LoyaltyCard> cards) {
        _fragment = fragment;
        _cards = cards;
    }

    @Override
    public LoyaltyCardsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.feed_item_card, parent, false);

        return new LoyaltyCardsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LoyaltyCardsViewHolder holder, int position) {
        final LoyaltyCard card = _cards.get(position);

        holder.cardNumber.setText(card.getCardNumber());
        holder.cardID.setText(card.getID());
        holder.retailerName.setText(card.getRetailer().getName());
        if ((card.getRetailer() != null) && (card.getRetailer().getIconURL() != null)) {
            Picasso.with(_fragment.getContext()).load(card.getRetailer().getIconURL()).into(holder.icon);
        }
        holder.linked.setChecked(card.isLinked());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(_fragment.getActivity());
                builder.setMessage(String.format("Unlink Card #: %s from %s", card.getCardNumber(), card.getRetailer().getName()))
                        .setTitle("Unlink Loyalty Card");
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoyaltyCardsManager.getInstance().unlinkLoyaltyCard(card.getID(), new LoyaltyCardsManager.OnLoyaltyCardUnlinkedListener() {
                            @Override
                            public void onUnlinked(SessionMError sessionMError) {
                                _fragment.fetchLinkedCards();
                            }
                        });
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(_fragment.getContext(), "You didn't unlink it", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _cards.size();
    }

    public static class LoyaltyCardsViewHolder extends RecyclerView.ViewHolder {
        TextView cardNumber;
        TextView retailerName;
        TextView cardID;
        ImageView icon;
        CheckBox linked;

        public LoyaltyCardsViewHolder(View v) {
            super(v);
            cardNumber = v.findViewById(R.id.card_number);
            retailerName = v.findViewById(R.id.retailer_name);
            cardID = v.findViewById(R.id.card_id);
            icon = v.findViewById(R.id.retailer_icon);
            linked = v.findViewById(R.id.linked_chb);
        }
    }
}
