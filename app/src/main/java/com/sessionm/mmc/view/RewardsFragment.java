/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.reward.RewardsListener;
import com.sessionm.api.reward.RewardsManager;
import com.sessionm.api.reward.data.offer.Offer;
import com.sessionm.api.reward.data.order.Order;
import com.sessionm.api.reward.data.skill.SkillChallenge;
import com.sessionm.api.reward.data.skill.SkillQuestion;
import com.sessionm.mmc.R;
import com.sessionm.mmc.controller.RewardsFeedListAdapter;

import java.util.ArrayList;
import java.util.List;

//Fragment of SessionM Rewards
public class RewardsFragment extends BaseScrollAndRefreshFragment {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private ObservableListView _listView;
    private RewardsFeedListAdapter _listAdapter;
    private List<Offer> _offers = new ArrayList<>();

    private RewardsManager _rewardsManager = SessionM.getInstance().getRewardsManager();

    public static RewardsFragment newInstance() {
        RewardsFragment f = new RewardsFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rewards, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _listView = (ObservableListView) rootView.findViewById(R.id.rewards_feed_list);
        _rewardsManager.setListener(_rewardsListener);
        _offers = _rewardsManager.getOffers();
        if (_offers == null) {
            _offers = new ArrayList<>();
        }
        _listAdapter = new RewardsFeedListAdapter(getActivity(), _offers);
        _listView.setAdapter(_listAdapter);

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RewardsFeedListAdapter.Row row = (RewardsFeedListAdapter.Row) _listAdapter.getItem(position);
                Offer offer = row._offer;
                Intent offerDetailsIntent = new Intent(getActivity(), OfferDetailsActivity.class);
                offerDetailsIntent.putExtra("offer_id", offer.getId());
                startActivity(offerDetailsIntent);
            }
        });

        _listView.setScrollViewCallbacks(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _rewardsManager.fetchOffers();
    }

    RewardsListener _rewardsListener = new RewardsListener() {
        @Override
        public void onOffersFetched(List<Offer> offers) {
            _swipeRefreshLayout.setRefreshing(false);
            RewardsFragment.this._offers.clear();
            if (offers == null) {
                offers = new ArrayList<>();
            }
            RewardsFragment.this._offers.addAll(offers);
            if (_listAdapter == null) {
                _listAdapter = new RewardsFeedListAdapter(getActivity(), RewardsFragment.this._offers);
                _listView.setAdapter(_listAdapter);
            }
            _listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onOrderPlaced(Order order) {

        }

        @Override
        public void onOrdersFetched(List<Order> list) {

        }

        @Override
        public void onSkillQuestionFetched(SkillQuestion skillQuestion) {

        }

        @Override
        public void onSkillQuestionAnswered(SkillChallenge skillChallenge) {

        }

        @Override
        public void onFailure(SessionMError error) {
            _swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        _rewardsManager.setListener(_rewardsListener);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        _rewardsManager.fetchOffers();
    }
}
