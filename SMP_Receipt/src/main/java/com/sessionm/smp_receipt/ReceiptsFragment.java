/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_receipt;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.core.api.SessionMError;
import com.sessionm.receipt.api.ReceiptsListener;
import com.sessionm.receipt.api.ReceiptsManager;
import com.sessionm.receipt.api.data.Receipt;
import com.sessionm.receipt.api.data.ReceiptResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReceiptsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private ReceiptsRecAdapter _listAdapter;
    private RecyclerView _recyclerView;

    private int position = 0;

    ReceiptsManager _receiptManager = ReceiptsManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.receipt_fragment, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _recyclerView = rootView.findViewById(R.id.receipts_feed_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _listAdapter = new ReceiptsRecAdapter(new ArrayList<>(_receiptManager.getReceipts()));
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
            refreshList(receipts);
        }

        @Override
        public void onProgress(Receipt receipt) {

        }

        @Override
        public void onFailure(SessionMError error) {
            _swipeRefreshLayout.setRefreshing(false);
            refreshList(new ArrayList<Receipt>());
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

    public void refreshList(List<Receipt> receipts) {
        _swipeRefreshLayout.setRefreshing(false);
        _listAdapter.replaceData(receipts);
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
        View dialogLayout = inflater.inflate(R.layout.receipt_image_dialog, null);
        final ImageView imageView = dialogLayout.findViewById(R.id.dialog_imageview);
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

    public class ReceiptsRecAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Receipt> _items;

        public ReceiptsRecAdapter(List<Receipt> items) {
            setList(items);
        }

        public void replaceData(List<Receipt> items) {
            setList(items);
            notifyDataSetChanged();
        }

        private void setList(List<Receipt> items) {
            _items = items;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getItemCount() {
            return _items.size();
        }

        public Receipt getItem(int i) {
            return _items.get(i);
        }

        @Override
        public ReceiptsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.receipt_item, parent, false);

            return new ReceiptsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            ReceiptsViewHolder holder = (ReceiptsViewHolder) viewHolder;
            final Receipt receipt = getItem(position);

            holder.textViewName.setText("ID: " + receipt.getID());
            holder.textViewStatus.setText("Status: " + receipt.getStatus().toString());
            holder.textViewCreateTime.setText("Created Time: " + receipt.getCreatedTime());
            holder.textViewUpdateTime.setText("Updated Time: " + receipt.getUpdatedTime());
            holder.textViewImageCount.setText("Image Count: " + receipt.getImageCount());
            if (receipt.getStatus().equals(Receipt.ReceiptStatus.VALID)) {
                String resultsString = "";
                for (ReceiptResult receiptResult : receipt.getResults()) {
                    resultsString += "\n" + receiptResult.toString();
                }
                holder.textViewValidPurchaseDate.setVisibility(View.VISIBLE);
                holder.textViewValidStoreName.setVisibility(View.VISIBLE);
                holder.textViewValidResults.setVisibility(View.VISIBLE);
                holder.textViewInvalidCode.setVisibility(View.GONE);
                holder.textViewInvalidReason.setVisibility(View.GONE);
                holder.textViewValidPurchaseDate.setText("Purchase Date: " + receipt.getReceiptDate());
                holder.textViewValidStoreName.setText("Store Name: " + receipt.getStoreName());
                holder.textViewValidResults.setText("Results: " + resultsString);
            } else if (receipt.getStatus().equals(Receipt.ReceiptStatus.INVALID)) {
                holder.textViewInvalidCode.setVisibility(View.VISIBLE);
                holder.textViewInvalidReason.setVisibility(View.VISIBLE);
                holder.textViewValidPurchaseDate.setVisibility(View.GONE);
                holder.textViewValidStoreName.setVisibility(View.GONE);
                holder.textViewValidResults.setVisibility(View.GONE);
                holder.textViewInvalidCode.setText("Invalid Code: " + receipt.getInvalidCode());
                holder.textViewInvalidReason.setText("Invalid Reason: " + receipt.getInvalidReason());
            } else {
                holder.textViewInvalidCode.setVisibility(View.GONE);
                holder.textViewInvalidReason.setVisibility(View.GONE);
                holder.textViewValidPurchaseDate.setVisibility(View.GONE);
                holder.textViewValidStoreName.setVisibility(View.GONE);
                holder.textViewValidResults.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (receipt.getImageCount() > 0 && receipt.getImageURLs().size() > 0) {
                        List<String> urls = receipt.getImageURLs();
                        popUpImageDialog(urls);
                    }
                }
            });
        }

        public class ReceiptsViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName;
            TextView textViewStatus;
            TextView textViewInvalidCode;
            TextView textViewInvalidReason;
            TextView textViewCreateTime;
            TextView textViewUpdateTime;
            TextView textViewImageCount;
            TextView textViewValidPurchaseDate;
            TextView textViewValidStoreName;
            TextView textViewValidResults;

            public ReceiptsViewHolder(View v) {
                super(v);
                textViewName = v.findViewById(R.id.receipt_name);
                textViewStatus = v.findViewById(R.id.receipt_status);
                textViewInvalidCode = v.findViewById(R.id.receipt_invalid_code);
                textViewInvalidReason = v.findViewById(R.id.receipt_invalid_reason);
                textViewCreateTime = v.findViewById(R.id.receipt_create_time);
                textViewUpdateTime = v.findViewById(R.id.receipt_update_time);
                textViewImageCount = v.findViewById(R.id.receipt_image_count);
                textViewValidPurchaseDate = v.findViewById(R.id.receipt_valid_purchase_date);
                textViewValidStoreName = v.findViewById(R.id.receipt_valid_store);
                textViewValidResults = v.findViewById(R.id.receipt_valid_results);
            }
        }
    }
}
