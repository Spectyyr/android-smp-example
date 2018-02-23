/*
* Copyright (c) 2016 SessionM. All rights reserved.
*/
package com.sessionm.smp_offers.store_offers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sessionm.api.SessionMError;
import com.sessionm.api.offers.OffersListener;
import com.sessionm.api.offers.OffersManager;
import com.sessionm.api.offers.data.results.claim.UserOfferClaimedResponse;
import com.sessionm.api.offers.data.results.purchase.OfferPurchasedResponse;
import com.sessionm.api.offers.data.results.store.StoreOffersFetchedResponse;
import com.sessionm.api.offers.data.results.user.UserOffersFetchedResponse;
import com.sessionm.smp_offers.R;

public class StoreOffersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Callback _callback;

    public interface Callback {
        void updatePoints(int points);
    }

    private final OffersManager offerManager = OffersManager.getInstance();

    public void fetchOffers() {
        _swipeRefreshLayout.setRefreshing(true);

        offerManager.setListener(offersListener);
        offerManager.fetchStoreOffers();
    }

    private SwipeRefreshLayout _swipeRefreshLayout;
    private StoreOffersRecAdapter _offersRecAdapter;
    private RecyclerView _recyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            _callback = (Callback) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.store_offers_fragment, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _recyclerView = (RecyclerView) rootView.findViewById(R.id.offers_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);

        _offersRecAdapter = new StoreOffersRecAdapter(this, _callback);
        _recyclerView.setAdapter(_offersRecAdapter);

        return rootView;
    }

    @Override
    public void onRefresh() {
        fetchOffers();
    }

    OffersListener offersListener = new OffersListener() {
        @Override
        public void onOfferPurchased(OfferPurchasedResponse offerPurchaseResult) {
        }

        @Override
        public void onUserOfferClaimed(UserOfferClaimedResponse userOfferClaimedResult) {
        }

        @Override
        public void onUserOffersFetched(UserOffersFetchedResponse userOffersResult) {
        }

        @Override
        public void onStoreOffersFetched(StoreOffersFetchedResponse storeOffersFetchedResponse) {
            if (_swipeRefreshLayout.isRefreshing()) {
                _swipeRefreshLayout.setRefreshing(false);
            }
            _offersRecAdapter.setOffers(storeOffersFetchedResponse.getOffers());
        }

        @Override
        public void onFailure(SessionMError sessionMError) {
            if (_swipeRefreshLayout.isRefreshing()) {
                _swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(StoreOffersFragment.this.getContext(), "Failure: '" + sessionMError.getCode() + "' " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

}

