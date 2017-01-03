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

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.transaction.TransactionsListener;
import com.sessionm.api.transaction.TransactionsManager;
import com.sessionm.api.transaction.data.Transaction;

import java.util.List;

public class TransactionsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private TransactionsRecAdapter _transactionsRecAdapter;
    private RecyclerView _recyclerView;

    TransactionsManager _transactionsManager = SessionM.getInstance().getTransactionsManager();
    private boolean noTransactions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _recyclerView = (RecyclerView) rootView.findViewById(R.id.transactions_feed_list);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _transactionsRecAdapter = new TransactionsRecAdapter(this);
        _recyclerView.setAdapter(_transactionsRecAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noTransactions = true;
        fetchAllTransactions();
    }

    TransactionsListener _transactionListener = new TransactionsListener() {
        @Override
        public void onTransactionsFetched(List<Transaction> transactions, boolean hasMore) {
            if (_swipeRefreshLayout.isRefreshing()) {
                _swipeRefreshLayout.setRefreshing(false);
            }
            _transactionsRecAdapter.addTransactions(transactions, noTransactions);
            noTransactions = false;
            if (hasMore)
                _transactionsManager.fetchMoreTransactions();
        }

        @Override
        public void onFailure(SessionMError error) {
            if (_swipeRefreshLayout.isRefreshing()) {
                _swipeRefreshLayout.setRefreshing(false);
            }
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
    }

    @Override
    public void onRefresh() {
        fetchAllTransactions();
    }

    private void fetchAllTransactions() {
        noTransactions = true;
        _transactionsManager.fetchTransactions(null, null, 50);
    }
}
