/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sessionm.api.receipt.data.Receipt;
import com.sessionm.api.receipt.data.ReceiptResult;
import com.sessionm.smp.R;
import com.sessionm.smp.view.ReceiptsFragment;

import java.util.List;

//Adapter class to draw Transaction List
public class ReceiptsFeedListAdapter extends RecyclerView.Adapter<ReceiptsFeedListAdapter.ReceiptsViewHolder> {

    private final ReceiptsFragment _fragment;
    private final List<Receipt> _receipts;

    public ReceiptsFeedListAdapter(ReceiptsFragment fragment, List<Receipt> receipts) {
        _fragment = fragment;
        _receipts = receipts;
    }

    @Override
    public ReceiptsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.feed_item_receipt, parent, false);

        return new ReceiptsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReceiptsViewHolder holder, int position) {
        final Receipt receipt = _receipts.get(position);
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
                    _fragment.popUpImageDialog(urls);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _receipts.size();
    }

    public static class ReceiptsViewHolder extends RecyclerView.ViewHolder {
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
            textViewName = (TextView) v.findViewById(R.id.receipt_name);
            textViewStatus = (TextView) v.findViewById(R.id.receipt_status);
            textViewInvalidCode = (TextView) v.findViewById(R.id.receipt_invalid_code);
            textViewInvalidReason = (TextView) v.findViewById(R.id.receipt_invalid_reason);
            textViewCreateTime = (TextView) v.findViewById(R.id.receipt_create_time);
            textViewUpdateTime = (TextView) v.findViewById(R.id.receipt_update_time);
            textViewImageCount = (TextView) v.findViewById(R.id.receipt_image_count);
            textViewValidPurchaseDate = (TextView) v.findViewById(R.id.receipt_valid_purchase_date);
            textViewValidStoreName = (TextView) v.findViewById(R.id.receipt_valid_store);
            textViewValidResults = (TextView) v.findViewById(R.id.receipt_valid_results);
        }
    }
}