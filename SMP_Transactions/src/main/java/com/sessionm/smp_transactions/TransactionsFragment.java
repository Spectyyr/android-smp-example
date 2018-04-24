/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */
package com.sessionm.smp_transactions;

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
import com.sessionm.transaction.api.TransactionsManager;
import com.sessionm.transaction.api.data.Transaction;

import java.util.List;

public class TransactionsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private TransactionsRecAdapter _transactionsRecAdapter;
    private RecyclerView _recyclerView;

    TransactionsManager _transactionsManager = TransactionsManager.getInstance();
    private boolean noTransactions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _recyclerView = rootView.findViewById(R.id.transactions_feed_list);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _transactionsRecAdapter = new TransactionsRecAdapter();
        _recyclerView.setAdapter(_transactionsRecAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noTransactions = true;
        fetchAllTransactions();
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
        fetchAllTransactions();
    }

    private void fetchAllTransactions() {
        noTransactions = true;
        _transactionsManager.fetchTransactions(null, null, 50, new TransactionsManager.OnTransactionsFetchedListener() {
            @Override
            public void onFetched(List<Transaction> list, boolean b, SessionMError sessionMError) {
                if (_swipeRefreshLayout.isRefreshing()) {
                    _swipeRefreshLayout.setRefreshing(false);
                }
                if (sessionMError != null) {
                    Toast.makeText(getActivity(), "Failed: " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    _transactionsRecAdapter.addTransactions(list, noTransactions);
                    noTransactions = false;
                    if (b)
                        _transactionsManager.fetchMoreTransactions();
                }
            }
        });
    }
}
