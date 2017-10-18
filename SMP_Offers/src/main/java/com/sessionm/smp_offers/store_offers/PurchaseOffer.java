package com.sessionm.smp_offers.store_offers;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.offers.OffersListener;
import com.sessionm.api.offers.OffersManager;
import com.sessionm.api.offers.data.results.claim.UserOfferClaimedResult;
import com.sessionm.api.offers.data.results.purchase.OfferPurchaseResult;
import com.sessionm.api.offers.data.results.store.OffersStoreResult;
import com.sessionm.api.offers.data.results.store.StoreOfferItem;
import com.sessionm.api.offers.data.results.user.UserOffersResult;
import com.sessionm.smp_offers.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class PurchaseOffer {
    private Activity _activity;
    private StoreOfferItem _item;
    private StoreOffersFragment.Callback _callback;

    public PurchaseOffer(Activity activity, StoreOffersFragment.Callback callback) {
        this._activity = activity;
        _callback = callback;
    }

    void purchase(final StoreOfferItem item) {
        _item = item;

        AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("TAG", "button dismiss");
            }
        });
        builder.setPositiveButton("Purchase Offer", null);

        LayoutInflater inflater = _activity.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.purchase_offer, null);

        Picasso.with(_activity).load(Uri.parse(_item.getMedia().get(0).getURI())).into((ImageView) dialogLayout.findViewById(R.id.imageView));
        ((TextView) dialogLayout.findViewById(R.id.title)).setText(item.getName());
        ((TextView) dialogLayout.findViewById(R.id.purchase_terms)).setText(item.getDescription() + "\n\n\n" + item.getTerms());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        ((TextView) dialogLayout.findViewById(R.id.purchase_range))
                .setText(String.format("This offer is available %s through %s",
                        item.getStartDate() != null ? df.format(item.getStartDate()) : " -- ",
                        item.getEndDate() != null ? df.format(item.getEndDate()) : " -- "
                ));

        SMPUser user = UserManager.getInstance().getCurrentUser();
        if (user != null) {
            ((TextView) dialogLayout.findViewById(R.id.purchase_available)).setText(String.format("%.0f", new Float(user.getAvailablePoints())));
            ((TextView) dialogLayout.findViewById(R.id.purchase_cost)).setText(String.format("%.0f", item.getPrice()));
            ((TextView) dialogLayout.findViewById(R.id.purcahse_amount)).setText(String.format("%.0f", (float) user.getAvailablePoints() - item.getPrice()));
        }

        AlertDialog dialog = builder.create();
        dialog.setView(dialogLayout);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("TAG", "" + dialog.getClass().toString());
                        OffersManager.getInstance().setListener(_purchaseListener);
                        OffersManager.getInstance().purchaseOffer(item.getID(), 1);
                    }
                });
            }
        });

        dialog.show();
    }

    OffersListener _purchaseListener = new OffersListener() {
        @Override public void onUserOfferClaimed(UserOfferClaimedResult claimedResult) {}
        @Override public void onUserOffersFetched(UserOffersResult userOffers) {}
        @Override public void onOffersStoreFetched(OffersStoreResult offersStore) {}

        @Override public void onOfferPurchased(OfferPurchaseResult purchase) {
            Toast.makeText(_activity, "Success: '" + purchase.getUserOffer().getID() + "' Name: '" + purchase.getUserOffer().getName() , Toast.LENGTH_SHORT).show();
            _callback.updatePoints(purchase.getPointsRemaining());
        }

        @Override
        public void onFailure(SessionMError error) {
            Toast.makeText(_activity, "Failure: '" + error.getCode() + "' " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
}