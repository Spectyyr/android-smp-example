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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sessionm.api.SessionMError;
import com.sessionm.api.offers.OffersListener;
import com.sessionm.api.offers.OffersManager;
import com.sessionm.api.offers.data.results.claim.UserOfferClaimedResult;
import com.sessionm.api.offers.data.results.purchase.OfferPurchaseResult;
import com.sessionm.api.offers.data.results.store.OffersStoreResult;
import com.sessionm.api.offers.data.results.user.UserOfferItem;
import com.sessionm.api.offers.data.results.user.UserOffersResult;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pmattheis on 10/2/17.
 */

class MyOffersRecAdapter extends RecyclerView.Adapter<MyOffersRecAdapter.OffersViewHolder> {

    private List<UserOfferItem> _offers = new ArrayList<>();
    private MyOffersFragment _fragment;
    private final OffersManager offerManager = OffersManager.getInstance();

    public MyOffersRecAdapter(MyOffersFragment fragment) {
        _fragment = fragment;
    }

    public void setOffers(List<UserOfferItem> offers) {
        _offers = offers;

        Collections.sort(_offers, new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return ((UserOfferItem) lhs).getAcquireDate().compareTo(((UserOfferItem) rhs).getAcquireDate());
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public MyOffersRecAdapter.OffersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_offer_item, parent, false);

        return new MyOffersRecAdapter.OffersViewHolder(itemView);
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public void onBindViewHolder(MyOffersRecAdapter.OffersViewHolder holder, int position) {
        final UserOfferItem offer = _offers.get(position);

        holder.name.setText(offer.getName());
        holder.expires.setText(dateFormat.format(offer.getExpirationDate()));

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
            expires = (TextView)v.findViewById(R.id.end_date);
            name = (TextView)v.findViewById(R.id.offer_name);
            media = (ImageView)v.findViewById(R.id.offer_media);
        }
    }

    private class ClaimOffer {
        private final Activity _activity;
        private UserOfferItem _offer;

        public ClaimOffer(Activity activity) {
            _activity = activity;
        }

        private Gson _gson = new Gson();
        public void redeem(UserOfferItem offer) {
            _offer = offer;
            offerManager.setListener(claimListener);
            offerManager.claimUserOffer(offer.getID());
        }

        private OffersListener claimListener = new OffersListener() {
            public Date _start;
            public Timer _timer;
            public TextView _validDates;

            @Override public void onOfferPurchased(OfferPurchaseResult purchase) {}
            @Override public void onUserOffersFetched(UserOffersResult userOffers) {}
            @Override public void onOffersStoreFetched(OffersStoreResult offersStore) {}

            @Override
            public void onUserOfferClaimed(UserOfferClaimedResult claimedResult) {
                AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
                builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("TAG", "button dismiss");
                    }
                });
                AlertDialog dialog = builder.create();
                View dialogLayout = _activity.getLayoutInflater().inflate(R.layout.claim_offer, null);

                Picasso.with(_activity).load(Uri.parse(claimedResult.getClaimedOffer().getCodeImageURI())).into((ImageView) dialogLayout.findViewById(R.id.barcode_image));
                Picasso.with(_activity).load(Uri.parse(_offer.getMedia().get(0).getURI())).into((ImageView) dialogLayout.findViewById(R.id.picture));

                ((TextView)dialogLayout.findViewById(R.id.barcode_text)).setText(claimedResult.getClaimedOffer().getCode());
                ((TextView)dialogLayout.findViewById(R.id.title)).setText(claimedResult.getClaimedOffer().getName());
                ((TextView)dialogLayout.findViewById(R.id.description)).setText(claimedResult.getClaimedOffer().getDescription());
                ((TextView)dialogLayout.findViewById(R.id.expiration_date)).setText("Expires: " + dateTimeFormat.format(claimedResult.getClaimedOffer().getCodeExpirationDateTime()));

                dialog.setView(dialogLayout);
                dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.show();

                _validDates = (TextView)dialogLayout.findViewById(R.id.valid_dates);
                _start = new Date();
                _timer = new Timer();
                _timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Date now = new Date();
                        long secs = (now.getTime() - _start.getTime()) / 1000;
                        Log.d("SessionM", "Tick: " + (60 - secs));
                    }
                }, 0, 1 * 1000);
            }

            @Override
            public void onFailure(SessionMError error) {
                Toast.makeText(_activity, "Failure: '" + error.getCode() + "' " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

    }
}
