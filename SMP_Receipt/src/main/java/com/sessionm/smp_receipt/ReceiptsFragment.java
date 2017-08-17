/*
* Copyright (c) 2016 SessionM. All rights reserved.
*/

package com.sessionm.smp_receipt;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.receipt.ReceiptsListener;
import com.sessionm.api.receipt.ReceiptsManager;
import com.sessionm.api.receipt.data.Receipt;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReceiptsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private ReceiptsRecAdapter _listAdapter;
    List<Receipt> _receipts;
    private RecyclerView _recyclerView;

    private int position = 0;

    ReceiptsManager _receiptManager = SessionM.getInstance().getReceiptsManager();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receipts, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _receipts = new ArrayList<>(_receiptManager.getReceipts());

        _recyclerView = (RecyclerView) rootView.findViewById(R.id.receipts_feed_list);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _listAdapter = new ReceiptsRecAdapter(this, _receipts);
        _recyclerView.setAdapter(_listAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _receiptManager.fetchReceipts(100, 1);
    }

    ReceiptsListener _receiptListener = new ReceiptsListener() {
        @Override
        public void onReceiptUploaded(Receipt receipt) {
        }

        @Override
        public void onReceiptsFetched(List<Receipt> receipts) {
            refreshListUI(receipts);
        }

        @Override
        public void onProgress(Receipt receipt) {

        }

        @Override
        public void onFailure(SessionMError error) {
            _swipeRefreshLayout.setRefreshing(false);
            refreshListUI(new ArrayList<Receipt>());
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
        _receiptManager.fetchReceipts(100, 1);
    }

    private void refreshListUI(List<Receipt> receipts) {
        _swipeRefreshLayout.setRefreshing(false);
        if (ReceiptsFragment.this._receipts == null) {
            ReceiptsFragment.this._receipts = new ArrayList<>();
        } else {
            ReceiptsFragment.this._receipts.clear();
        }
        ReceiptsFragment.this._receipts.addAll(receipts);
        _listAdapter.notifyDataSetChanged();
    }

    public void popUpImageDialog(final List<String> urls) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                position = 0;
            }
        });
        AlertDialog dialog = builder.create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_image, null);
        final ImageView imageView = (ImageView) dialogLayout.findViewById(R.id.dialog_imageview);
        Picasso.with(getActivity())
                .load(urls.get(0))
                .resize(1280, 800)
                .onlyScaleDown()
                .centerInside()
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urls.size() > 1) {
                    if (position < urls.size() - 1)
                        position += 1;
                    else
                        position = 0;
                    Picasso.with(getActivity())
                            .load(urls.get(position))
                            .resize(1280, 800)
                            .onlyScaleDown()
                            .centerInside()
                            .into(imageView);
                }
            }
        });

        dialog.setView(dialogLayout);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                position = 0;
            }
        });

        dialog.show();
    }
}
