/*
* Copyright (c) 2016 SessionM. All rights reserved.
*/

package com.sessionm.mmc.view;

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
import com.sessionm.api.receipt.ReceiptsListener;
import com.sessionm.api.receipt.ReceiptsManager;
import com.sessionm.api.receipt.data.Receipt;
import com.sessionm.mmc.R;
import com.sessionm.mmc.controller.ReceiptsFeedListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReceiptsFragment extends BaseScrollAndRefreshFragment {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private ObservableListView _listView;
    private ReceiptsFeedListAdapter _listAdapter;
    List<Receipt> _receipts;

    ReceiptsManager _receiptManager = SessionM.getInstance().getReceiptManager();

    public static ReceiptsFragment newInstance() {
        ReceiptsFragment f = new ReceiptsFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receipts, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _listView = (ObservableListView) rootView.findViewById(R.id.receipts_feed_list);
        _receipts = new ArrayList<>(_receiptManager.getReceipts());
        _listAdapter = new ReceiptsFeedListAdapter(getActivity(), _receipts);
        _listView.setAdapter(_listAdapter);

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Receipt receipt = _receipts.get(position);
                if (receipt.getImageCount() > 0 && receipt.getImageURLs().size() > 0) {
                    List<String> urls = receipt.getImageURLs();
                    popUpImageDialog(urls);
                }
            }
        });

        _listView.setScrollViewCallbacks(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _receiptManager.fetchReceipts();
    }

    ReceiptsListener _receiptListener = new ReceiptsListener() {
        @Override
        public void onReceiptUploaded(Receipt receipt) {
        }

        @Override
        public void onReceiptsFetched(List<Receipt> receipts) {
            _swipeRefreshLayout.setRefreshing(false);
            if (ReceiptsFragment.this._receipts == null) {
                ReceiptsFragment.this._receipts = new ArrayList<>();
            } else {
                ReceiptsFragment.this._receipts.clear();
            }
            ReceiptsFragment.this._receipts.addAll(receipts);
            if (_listAdapter == null) {
                _listAdapter = new ReceiptsFeedListAdapter(getActivity(), ReceiptsFragment.this._receipts);
                _listView.setAdapter(_listAdapter);
            }
            _listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onProgress(Receipt receipt) {

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
        _receiptManager.setListener(_receiptListener);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        _receiptManager.fetchReceipts();
    }
}
