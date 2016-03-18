/*
 * Copyright (c) 2015 SessionM. All rights reserved.
 */

package com.sessionm.mmc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sessionm.mmc.controller.OrdersFeedListAdapter;

import java.util.ArrayList;
import java.util.List;

//Fragment of SessionM Rewards
public class OrdersFragment extends BaseScrollAndRefreshFragment {

    private static final String TAG = "FeedListActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ObservableListView listView;
    private OrdersFeedListAdapter listAdapter;
    private List<Order> _orders;

    private RewardsManager _rewardsManager;

    public static OrdersFragment newInstance() {
        OrdersFragment f = new OrdersFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        ViewCompat.setElevation(rootView, 50);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        listView = (ObservableListView) rootView.findViewById(R.id.orders_feed_list);
        _rewardsManager = SessionM.getInstance().getRewardsManager();
        _orders = _rewardsManager.getOrders();
        if (_orders != null) {
            listAdapter = new OrdersFeedListAdapter(getActivity(), _orders);
            listView.setAdapter(listAdapter);
        }

        listView.setScrollViewCallbacks(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _rewardsManager.fetchOrders();
    }

    RewardsListener _RewardsListener = new RewardsListener() {
        @Override
        public void onOffersFetched(List<Offer> list) {

        }

        @Override
        public void onOrderPlaced(Order order) {

        }

        @Override public void onOrdersFetched(List<Order> orders) {
            swipeRefreshLayout.setRefreshing(false);
            if (_orders == null) {
                _orders = new ArrayList<>();
            } else {
                _orders.clear();
            }
            _orders.addAll(orders);
            if (listAdapter == null) {
                listAdapter = new OrdersFeedListAdapter(getActivity(), _orders);
                listView.setAdapter(listAdapter);
            }
            listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onSkillQuestionFetched(SkillQuestion skillQuestion) {

        }

        @Override
        public void onSkillQuestionAnswered(SkillChallenge skillChallenge) {

        }

        @Override
        public void onFailure(SessionMError error) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        _rewardsManager.setListener(_RewardsListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        _rewardsManager.setListener(null);
    }

    @Override
    public void onRefresh() {
        _rewardsManager.fetchOrders();
    }
}
