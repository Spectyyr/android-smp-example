/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sessionm.api.receipt.data.Receipt;
import com.sessionm.api.receipt.data.ReceiptResult;
import com.sessionm.mmc.R;

import java.util.List;

//Adapter class to draw Transaction List
public class ReceiptsFeedListAdapter extends BaseAdapter {

    private final Context _context;
    private final List<Receipt> _receipts;
    private LayoutInflater _inflater;

    public ReceiptsFeedListAdapter(Context context, List<Receipt> receipts) {
        _context = context;
        _receipts = receipts;
    }

    @Override
    public int getCount() {
        return _receipts.size();
    }

    @Override
    public Object getItem(int position) {
        return _receipts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            if (_inflater == null)
                _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = _inflater.inflate(R.layout.receipt_row, parent, false);
            holder = new ViewHolder();
            holder.textViewName = (TextView) convertView.findViewById(R.id.receipt_name);
            holder.textViewStatus = (TextView) convertView.findViewById(R.id.receipt_status);
            holder.textViewInvalidCode = (TextView) convertView.findViewById(R.id.receipt_invalid_code);
            holder.textViewInvalidReason = (TextView) convertView.findViewById(R.id.receipt_invalid_reason);
            holder.textViewCreateTime = (TextView) convertView.findViewById(R.id.receipt_create_time);
            holder.textViewUpdateTime = (TextView) convertView.findViewById(R.id.receipt_update_time);
            holder.textViewImageCount = (TextView) convertView.findViewById(R.id.receipt_image_count);
            holder.textViewValidPurchaseDate = (TextView) convertView.findViewById(R.id.receipt_valid_purchase_date);
            holder.textViewValidStoreName = (TextView) convertView.findViewById(R.id.receipt_valid_store);
            holder.textViewValidResults = (TextView) convertView.findViewById(R.id.receipt_valid_results);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (_receipts != null && _receipts.size() > 0) {
            Receipt a = _receipts.get(position);
            holder.textViewName.setText("ID: " + a.getID());
            holder.textViewStatus.setText("Status: " + a.getStatus().toString());
            holder.textViewCreateTime.setText("Created Time: " + a.getCreatedTime());
            holder.textViewUpdateTime.setText("Updated Time: " + a.getUpdatedTime());
            holder.textViewImageCount.setText("Image Count: " + a.getImageCount());
            if (a.getStatus().equals(Receipt.ReceiptStatus.VALID)) {
                String resultsString = "";
                for (ReceiptResult receiptResult : a.getResults()) {
                    resultsString += "\n" + receiptResult.toString();
                }
                holder.textViewValidPurchaseDate.setVisibility(View.VISIBLE);
                holder.textViewValidStoreName.setVisibility(View.VISIBLE);
                holder.textViewValidResults.setVisibility(View.VISIBLE);
                holder.textViewInvalidCode.setVisibility(View.GONE);
                holder.textViewInvalidReason.setVisibility(View.GONE);
                holder.textViewValidPurchaseDate.setText("Purchase Date: " + a.getReceiptDate());
                holder.textViewValidStoreName.setText("Store Name: " + a.getStoreName());
                holder.textViewValidResults.setText("Results: " + resultsString);
            } else if (a.getStatus().equals(Receipt.ReceiptStatus.INVALID)) {
                holder.textViewInvalidCode.setVisibility(View.VISIBLE);
                holder.textViewInvalidReason.setVisibility(View.VISIBLE);
                holder.textViewValidPurchaseDate.setVisibility(View.GONE);
                holder.textViewValidStoreName.setVisibility(View.GONE);
                holder.textViewValidResults.setVisibility(View.GONE);
                holder.textViewInvalidCode.setText("Invalid Code: " + a.getInvalidCode());
                holder.textViewInvalidReason.setText("Invalid Reason: " + a.getInvalidReason());
            }
        }
        return convertView;
    }

    private static class ViewHolder {
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
    }
}