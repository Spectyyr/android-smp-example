package com.sessionm.smp_offers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sessionm.core.Util;
import com.sessionm.core.offers.data.results.user.CoreUserOffersResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pmattheis on 10/2/17.
 */

public class MyOffersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private StoreOffersFragment.Callbacks _contextListener;
    private final OffersManager offerManager = OffersManager.getInstance();

    public void fetchOffers() {
        if (_swipeRefreshLayout != null) { _swipeRefreshLayout.setRefreshing(true); }
        offerManager.setListener(offersListener);
        offerManager.fetchUserOffers();
    }

    interface Callbacks {

    }

    private SwipeRefreshLayout _swipeRefreshLayout;
    private MyOffersRecAdapter _offersRecAdapter;
    private RecyclerView _recyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        _contextListener = (StoreOffersFragment.Callbacks) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_offers_fragment, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _recyclerView = (RecyclerView) rootView.findViewById(R.id.offers_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _offersRecAdapter = new MyOffersRecAdapter(this);
        _recyclerView.setAdapter(_offersRecAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRefresh() { fetchOffers(); }

    OffersListener offersListener = new OffersListener() {
        @Override public void onOfferPurchased(OfferPurchaseResult offerPurchaseResult) {}

        @Override public void onUserOfferClaimed(UserOfferClaimedResult userOfferClaimedResult) {}

        @Override
        public void onUserOffersFetched(UserOffersResult userOffersResult) {
            if (_swipeRefreshLayout.isRefreshing()) {
                _swipeRefreshLayout.setRefreshing(false);
            }
            _offersRecAdapter.setOffers(userOffersResult.getUserOffers());
            //_offersRecAdapter.setOffers(makeAnOffer());
        }

        @Override public void onOffersStoreFetched(OffersStoreResult offersStoreResult) {}

        @Override
        public void onFailure(SessionMError sessionMError) {
            if (_swipeRefreshLayout.isRefreshing()) {
                _swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(MyOffersFragment.this.getContext(), "Failure: '" + sessionMError.getCode() + "' " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    Gson _gson = new Gson();

    private List<UserOfferItem> makeAnOffer() {
        return new CoreUserOffersResult(Util.deNull(_gson.fromJson("{\n" +
                "  \"status\": 200,\n" +
                "  \"message\": null,\n" +
                "  \"response_payload\": {\n" +
                "    \"user_offers\": [\n" +
                "      {\n" +
                "        \"offer_id\": \"1036e3cd-9fb6-4019-beed-0c4b559ea280\",\n" +
                "        \"offer_group_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
                "        \"offer_type\": \"fixed_amount_discount\",\n" +
                "        \"start_date\": \"2017-09-01T00:00:00\",\n" +
                "        \"end_date\": \"2017-12-01T00:00:00\",\n" +
                "        \"acquire_date\": \"2017-09-14T18:42:11.8121313\",\n" +
                "        \"expiration_date\": \"2017-10-14T18:42:11.7923819\",\n" +
                "        \"redeem_date\": null,\n" +
                "        \"is_redeemable\": true,\n" +
                "        \"id\": \"2333b878-58aa-4d18-bcd3-51e4b80f9e7f\",\n" +
                "        \"name\": \"Paul Offer $1\",\n" +
                "        \"description\": \"This is an Offer for $1 off fries\",\n" +
                "        \"terms\": \"None\",\n" +
                "        \"media\": [\n" +
                "          {\n" +
                "            \"id\": \"ec83324b-8c3c-44df-82e1-71b7b78313a7\",\n" +
                "            \"offer_id\": null,\n" +
                "            \"uri\": \"http://cdn.loyaltr.ee/mobile/568D75D8-F935-4A91-855B-9AE6952D5127/item_B62CB79F-1CE7-4D80-86EA-F844804803BE.png\",\n" +
                "            \"category_id\": \"e6e8d3f0-8e2a-4cac-ba78-b7c3481c2bac\",\n" +
                "            \"category_name\": \"OffersStore\",\n" +
                "            \"content_type\": 1,\n" +
                "            \"culture\": \"en\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"offer_id\": \"5bf574c6-a833-4592-9ee4-ccb2c252fbcf\",\n" +
                "        \"offer_group_id\": \"00000000-0000-0000-0000-000000000000\",\n" +
                "        \"offer_type\": \"buy_x_get_y_fixed\",\n" +
                "        \"start_date\": \"2017-09-01T00:00:00\",\n" +
                "        \"end_date\": null,\n" +
                "        \"acquire_date\": \"2017-09-25T20:58:59.8416519\",\n" +
                "        \"expiration_date\": \"2017-10-25T20:58:59.8260071\",\n" +
                "        \"redeem_date\": null,\n" +
                "        \"is_redeemable\": true,\n" +
                "        \"id\": \"15bb2322-1b8e-4f68-a429-69bcc9993aab\",\n" +
                "        \"name\": \"Would you like fries with that?\",\n" +
                "        \"description\": \"Ever wonder what happened to the \\\"cool\\\" kids in HS?\",\n" +
                "        \"terms\": \"None\",\n" +
                "        \"media\": [\n" +
                "          {\n" +
                "            \"id\": \"ec83324b-8c3c-44df-82e1-71b7b78313a7\",\n" +
                "            \"offer_id\": null,\n" +
                "            \"uri\": \"http://cdn.loyaltr.ee/mobile/568D75D8-F935-4A91-855B-9AE6952D5127/item_B62CB79F-1CE7-4D80-86EA-F844804803BE.png\",\n" +
                "            \"category_id\": \"e6e8d3f0-8e2a-4cac-ba78-b7c3481c2bac\",\n" +
                "            \"category_name\": \"OffersStore\",\n" +
                "            \"content_type\": 1,\n" +
                "            \"culture\": \"en\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ],\n" +
                "    \"offer_groups\": [\n" +
                "      {\n" +
                "        \"id\": \"00000000-0000-0000-0000-000000000000\",\n" +
                "        \"name\": \"All Offers\",\n" +
                "        \"media\": null,\n" +
                "        \"sort_order\": 0\n" +
                "      }\n" +
                "    ],\n" +
                "    \"offer_categories\": [\n" +
                "      {\n" +
                "        \"id\": \"3c79691f-eb19-4d0d-898b-74d59eef28fe\",\n" +
                "        \"name\": \"All Offers\",\n" +
                "        \"offer_types\": [\n" +
                "          \"percent_discount\",\n" +
                "          \"fixed_amount_discount\",\n" +
                "          \"set_price\",\n" +
                "          \"raffle\",\n" +
                "          \"buy_x_get_y_percent\",\n" +
                "          \"buy_x_get_y_fixed\",\n" +
                "          \"buy_x_get_y_set_price\"\n" +
                "        ],\n" +
                "        \"sort_order\": 0\n" +
                "      }\n" +
                "    ],\n" +
                "    \"total_points\": 20000,\n" +
                "    \"available_points\": 19958\n" +
                "  },\n" +
                "  \"stack_trace\": null\n" +
                "}", HashMap.class))).getUserOffers();
    }
}
