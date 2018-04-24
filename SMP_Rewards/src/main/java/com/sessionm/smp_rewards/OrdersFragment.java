/*
 * Copyright (c) 2016 SessionM. All rights reserved.
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
import com.sessionm.reward.api.data.order.Order;

import java.util.ArrayList;
import java.util.List;

//Fragment of SessionM Rewards
public class OrdersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private OrdersFeedListAdapter _listAdapter;
    private List<Order> _orders;
    private RecyclerView _recyclerView;

    private RewardsManager _rewardsManager = RewardsManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _orders = new ArrayList<>(_rewardsManager.getOrders());

        _recyclerView = rootView.findViewById(R.id.orders_feed_list);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _listAdapter = new OrdersFeedListAdapter(_orders);
        _recyclerView.setAdapter(_listAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _rewardsManager.fetchOrders();
    }

    @Override
    public void onRefresh() {
        _rewardsManager.fetchOrders(new RewardsManager.OnOrdersFetchedlistener() {
            @Override
            public void onOrdersFetched(List<Order> list, SessionMError sessionMError) {
                _swipeRefreshLayout.setRefreshing(false);
                if (sessionMError != null)
                    Toast.makeText(getActivity(), "Failed: " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                else {
                    if (OrdersFragment.this._orders == null) {
                        OrdersFragment.this._orders = new ArrayList<>();
                    } else {
                        OrdersFragment.this._orders.clear();
                    }
                    OrdersFragment.this._orders.addAll(list);
                    _listAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
