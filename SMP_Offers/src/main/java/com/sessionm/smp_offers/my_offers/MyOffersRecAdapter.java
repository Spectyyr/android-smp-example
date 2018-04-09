package com.sessionm.smp_offers.my_offers;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sessionm.api.offers.data.results.user.UserOfferItem;
import com.sessionm.smp_offers.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class MyOffersRecAdapter extends RecyclerView.Adapter<MyOffersRecAdapter.OffersViewHolder> {

    private List<UserOfferItem> _offers = new ArrayList<>();
    private MyOffersFragment _fragment;

    public MyOffersRecAdapter(MyOffersFragment fragment) {
        _fragment = fragment;
    }

    public void setOffers(List<UserOfferItem> offers) {
        _offers = offers;

        notifyDataSetChanged();
    }

    @Override
    public MyOffersRecAdapter.OffersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_offer_item, parent, false);

        return new MyOffersRecAdapter.OffersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyOffersRecAdapter.OffersViewHolder holder, int position) {
        final UserOfferItem offer = _offers.get(position);

        holder.name.setText(offer.getName());
        holder.expires.setText(offer.getExpirationDate());

        Picasso.with(holder.itemView.getContext()).load(Uri.parse(offer.getMedia().get(0).getURI())).into(holder.media);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new ClaimOffer(_fragment.getActivity())).redeem(offer);
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
        public TextView expires;

        public OffersViewHolder(View v) {
            super(v);
            expires = v.findViewById(R.id.expires_date);
            name = v.findViewById(R.id.offer_name);
            media = v.findViewById(R.id.offer_media);
        }
    }
}
