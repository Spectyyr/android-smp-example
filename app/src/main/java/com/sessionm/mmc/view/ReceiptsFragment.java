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
import android.widget.AdapterView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.receipt.ReceiptsListener;
import com.sessionm.api.receipt.ReceiptsManager;
import com.sessionm.api.receipt.data.Receipt;
import com.sessionm.mmc.R;
import com.sessionm.mmc.controller.ReceiptsFeedListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReceiptsFragment extends BaseScrollAndRefreshFragment {
    private static final String TAG = "ReceiptsListActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ObservableListView listView;
    private ReceiptsFeedListAdapter listAdapter;
    List<Receipt> _receipts;

    ReceiptsManager _receiptManager;

    public static ReceiptsFragment newInstance() {
        ReceiptsFragment f = new ReceiptsFragment();
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
        _receiptManager = SessionM.getInstance().getReceiptManager();
        _receipts = _receiptManager.getReceipts();
        if (_receipts != null) {
            listAdapter = new ReceiptsFeedListAdapter(getActivity(), _receipts);
            listView.setAdapter(listAdapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Receipt receipt = _receipts.get(position);
                if (receipt.getImageCount() > 0 && receipt.getImageUrls().size() > 0) {
                    String url = receipt.getImageUrls().get(0);
                    popUpImageDialog(url);
                }
            }
        });

        listView.setScrollViewCallbacks(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _receiptManager.fetchReceipts();
    }

    ReceiptsListener _receiptListener = new ReceiptsListener() {
        @Override
        public void onReceiptsUploaded(Receipt receipt) {
        }

        @Override
        public void onReceiptsFetched(List<Receipt> receipts) {
            swipeRefreshLayout.setRefreshing(false);
            if (_receipts == null) {
                _receipts = new ArrayList<>();
            } else {
                _receipts.clear();
            }
            _receipts.addAll(receipts);
            if (listAdapter == null) {
                listAdapter = new ReceiptsFeedListAdapter(getActivity(), _receipts);
                listView.setAdapter(listAdapter);
            }
            listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onProgress(int i) {

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
        _receiptManager.setListener(_receiptListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        _receiptManager.setListener(null);
    }

    @Override
    public void onRefresh() {
        _receiptManager.fetchReceipts();
    }
}
