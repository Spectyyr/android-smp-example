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
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.offers.OffersListener;
import com.sessionm.api.offers.OffersManager;
import com.sessionm.api.offers.data.builders.ClaimUserOfferRequestBuilder;
import com.sessionm.api.offers.data.results.claim.UserOfferClaimedResult;
import com.sessionm.api.offers.data.results.purchase.OfferPurchaseResult;
import com.sessionm.api.offers.data.results.store.OffersStoreResult;
import com.sessionm.api.offers.data.results.user.UserOfferItem;
import com.sessionm.api.offers.data.results.user.UserOffersResult;
import com.sessionm.core.Util;
import com.sessionm.core.offers.data.results.claim.CoreUserOfferClaimedResult;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
                (new RedeemOffer(_fragment.getActivity())).redeem(offer);
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

    private class RedeemOffer {
        private final Activity _activity;

        public RedeemOffer(Activity activity) {
            _activity = activity;
        }

        private Gson _gson = new Gson();
        public void redeem(UserOfferItem offer) {
            offerManager.setListener(claimListener);
            offerManager.claimUserOffer((new ClaimUserOfferRequestBuilder(offer.getID())).build());
            /*
            claimListener.onUserOfferClaimed(new CoreUserOfferClaimedResult(Util.deNull(_gson.fromJson(
                    "{" +
                    "   \"status\" : \"ok\"," +
                    "   \"response_payload\" : {" +
                    "      \"claimed_offer\": {" +
                    "         \"code\" : \"046386\"," +
                    "         \"code_image_uri\" : \"https://www.barcodesinc.com/generator/image.php?code=046386&style=197&type=C128B&width=128&height=50&xres=1&font=3\"," +
                    "         \"server_date_time\" : \"2017-10-14T08:40:13\"," +
                    "         \"code_expiration_date_time\" : \"2017-10-14T18:42:11.8121313\"," +
                    "         \"name\" : \"Your Bar Code\"," +
                    "         \"description\" : \"Something lengthy\"" +
                    "      }" +
                    "   }" +
                    "}"
                    , HashMap.class))));
                    */
        }

        private OffersListener claimListener = new OffersListener() {
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
                View dialogLayout = _activity.getLayoutInflater().inflate(R.layout.redeem_offer, null);

                RotateAnimation anim = new RotateAnimation(0, 360f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
                anim.setInterpolator(new LinearInterpolator());
                anim.setDuration(8 * 1000);
                anim.setRepeatCount(Animation.INFINITE);
                ((ImageView)dialogLayout.findViewById(R.id.spinny)).setAnimation(anim);

                Picasso.with(_activity).load(Uri.parse(claimedResult.getClaimedOffer().getCodeImageURI())).into((ImageView) dialogLayout.findViewById(R.id.barcode_image));

                ((TextView)dialogLayout.findViewById(R.id.barcode_text)).setText(claimedResult.getClaimedOffer().getCode());
                ((TextView)dialogLayout.findViewById(R.id.title)).setText(claimedResult.getClaimedOffer().getName());
                ((TextView)dialogLayout.findViewById(R.id.description)).setText(claimedResult.getClaimedOffer().getDescription());
                ((TextView)dialogLayout.findViewById(R.id.expiration_date)).setText("Expires: " + dateTimeFormat.format(claimedResult.getClaimedOffer().getCodeExpirationDateTime()));

                dialog.setView(dialogLayout);
                dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.show();
            }

            @Override
            public void onFailure(SessionMError error) {
                Toast.makeText(_activity, "Failure: '" + error.getCode() + "' " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

    }
}
