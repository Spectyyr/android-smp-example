/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_rewards;

import android.os.Bundle;
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

import com.sessionm.core.api.SessionMError;
import com.sessionm.reward.api.RewardsManager;
import com.sessionm.reward.api.data.offer.Offer;

import java.util.ArrayList;
import java.util.List;

//Fragment of SessionM Rewards
public class RewardsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private List<Offer> _offers = new ArrayList<>();
    private RewardsRecAdapter _rewardsRecAdapter;
    private RecyclerView _recyclerView;

    private RewardsManager _rewardsManager = RewardsManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rewards, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _recyclerView = rootView.findViewById(R.id.rewards_feed_list);
        _offers = new ArrayList<>(_rewardsManager.getOffers());
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _rewardsRecAdapter = new RewardsRecAdapter(this, _offers);
        _recyclerView.setAdapter(_rewardsRecAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _rewardsManager.fetchOffers();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        _rewardsManager.fetchOffers(new RewardsManager.OnOffersFetchedListener() {
            @Override
            public void onOffersFetched(List<Offer> offers, SessionMError sessionMError) {
                _swipeRefreshLayout.setRefreshing(false);
                if (sessionMError != null)
                    Toast.makeText(getActivity(), "Failed: " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                else {
                    _offers.clear();
                    if (offers == null) {
                        offers = new ArrayList<>();
                    }
                    _offers.addAll(offers);
                    _rewardsRecAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
