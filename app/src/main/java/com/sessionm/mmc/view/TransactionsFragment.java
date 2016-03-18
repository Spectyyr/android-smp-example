/*
* Copyright (c) 2016 SessionM. All rights reserved.
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
import com.sessionm.api.transaction.TransactionsListener;
import com.sessionm.api.transaction.TransactionsManager;
import com.sessionm.api.transaction.data.Transaction;
import com.sessionm.mmc.R;
import com.sessionm.mmc.controller.TransactionsFeedListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends BaseScrollAndRefreshFragment{
    private static final String TAG = "FeedListActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ObservableListView listView;
    private TransactionsFeedListAdapter listAdapter;
    List<Transaction> _transactions = new ArrayList<>();

    TransactionsManager _transactionsManager;

    public static TransactionsFragment newInstance() {
        TransactionsFragment f = new TransactionsFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);
        ViewCompat.setElevation(rootView, 50);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        listView = (ObservableListView) rootView.findViewById(R.id.transactions_feed_list);
        _transactionsManager = SessionM.getInstance().getTransactionsManager();
        _transactions = _transactionsManager.getTransactions();
        if (_transactions != null) {
            listAdapter = new TransactionsFeedListAdapter(getActivity(), _transactions);
            listView.setAdapter(listAdapter);
        }
        listView.setScrollViewCallbacks(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _transactionsManager.fetchTransactions();
    }

    TransactionsListener _transactionListener = new TransactionsListener() {
        @Override
        public void onFetchTransactionResult(List<Transaction> transactions, boolean hasMore) {
            swipeRefreshLayout.setRefreshing(false);
            if (listAdapter == null) {
                listAdapter = new TransactionsFeedListAdapter(getActivity(), _transactions);
                listView.setAdapter(listAdapter);
            }
            listAdapter.notifyDataSetChanged();
            if (hasMore)
                _transactionsManager.fetchMoreTransactions();
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
        _transactionsManager.setListener(_transactionListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        _transactionsManager.setListener(null);
    }

    @Override
    public void onRefresh() {
        _transactionsManager.fetchTransactions(null, null, 5);
    }
}
