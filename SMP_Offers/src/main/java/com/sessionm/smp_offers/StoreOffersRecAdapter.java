/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_offers;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.loyaltycard.LoyaltyCardsListener;
import com.sessionm.api.loyaltycard.LoyaltyCardsManager;
import com.sessionm.api.loyaltycard.data.LoyaltyCard;
import com.sessionm.api.loyaltycard.data.LoyaltyCardTransaction;
import com.sessionm.api.loyaltycard.data.Retailer;
import com.sessionm.api.offers.data.results.store.StoreOfferItem;
import com.sessionm.api.receipt.ReceiptsListener;
import com.sessionm.api.receipt.ReceiptsManager;
import com.sessionm.api.receipt.data.Receipt;
import com.sessionm.api.receipt.data.ReceiptResult;
import com.sessionm.api.transaction.data.Transaction;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

// Adapter class to draw Offers List
public class StoreOffersRecAdapter extends RecyclerView.Adapter<StoreOffersRecAdapter.OffersViewHolder> {

    private List<StoreOfferItem> _offers = new ArrayList<>();
    private StoreOffersFragment _fragment;

    public StoreOffersRecAdapter(StoreOffersFragment fragment) {
        _fragment = fragment;
    }

    public void setOffers(List<StoreOfferItem> offers) {
        _offers = offers;

        Collections.sort(_offers, new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return ((StoreOfferItem) lhs).getStartDate().compareTo(((StoreOfferItem) rhs).getStartDate());
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public OffersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_offer_item, parent, false);

        return new OffersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OffersViewHolder holder, int position) {
        final StoreOfferItem offer = _offers.get(position);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        holder.name.setText(offer.getName());
        holder.points.setText(String.format("%.0f", offer.getPrice()));
        holder.startDate.setText(offer.getStartDate() != null ? df.format(offer.getStartDate()) : " --");
        holder.endDate.setText(offer.getEndDate() != null ? df.format(offer.getEndDate()) : " --");

        Picasso.with(holder.itemView.getContext()).load(Uri.parse(offer.getMedia().get(0).getURI())).into(holder.media);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new PurchaseOffer(_fragment.getActivity())).purchase(offer);
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

    public class OffersViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final ImageView media;
        private final TextView points;
        private final TextView startDate;
        private final TextView endDate;

        public OffersViewHolder(View v) {
            super(v);
            startDate = (TextView)v.findViewById(R.id.end_date);
            endDate = (TextView)v.findViewById(R.id.start_date);
            name = (TextView)v.findViewById(R.id.offer_name);
            media = (ImageView)v.findViewById(R.id.offer_media);
            points = (TextView)v.findViewById(R.id.offer_points);
        }
    }

    private static class PurchaseOffer {
        private Activity _activity;
        private StoreOfferItem _item;

        public PurchaseOffer(Activity activity) {
            this._activity = activity;
        }

        private void purchase(StoreOfferItem item) {
            _item = item;

            AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
            builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("TAG", "button dismiss");
                }
            });
            builder.setPositiveButton("Purchase Offer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("TAG", "button purchase");
                }
            });

            AlertDialog dialog = builder.create();
            LayoutInflater inflater = _activity.getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.purchase_offer, null);

            dialog.setView(dialogLayout);
            dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

            Picasso.with(_activity).load(Uri.parse(_item.getMedia().get(0).getURI())).into((ImageView) dialogLayout.findViewById(R.id.imageView));
            ((TextView)dialogLayout.findViewById(R.id.title)).setText(item.getName());
            ((TextView)dialogLayout.findViewById(R.id.purchase_terms)).setText(item.getDescription() + "\n" + item.getTerms());

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            ((TextView)dialogLayout.findViewById(R.id.purchase_range))
                    .setText(String.format("This offers is available %s through %s",
                                item.getStartDate() != null ? df.format(item.getStartDate()) : " -- ",
                                item.getEndDate() != null ? df.format(item.getEndDate()) : " -- "
                            ));

            SMPUser user = UserManager.getInstance().getCurrentUser();
            if (user != null) {
                ((TextView)dialogLayout.findViewById(R.id.purchase_available)).setText(String.format("%d", user.getAvailablePoints()));
            }

            dialog.show();
        }
    }
}