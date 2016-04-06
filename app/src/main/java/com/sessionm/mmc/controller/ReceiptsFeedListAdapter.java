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
        if (_inflater == null)
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = _inflater.inflate(R.layout.receipt_row, parent, false);

        TextView textViewName = (TextView) convertView.findViewById(R.id.receipt_name);
        TextView textViewStatus = (TextView) convertView.findViewById(R.id.receipt_status);
        TextView textViewInvalidCode = (TextView) convertView.findViewById(R.id.receipt_invalid_code);
        TextView textViewInvalidReason = (TextView) convertView.findViewById(R.id.receipt_invalid_reason);
        TextView textViewCreateTime = (TextView) convertView.findViewById(R.id.receipt_create_time);
        TextView textViewUpdateTime = (TextView) convertView.findViewById(R.id.receipt_update_time);
        TextView textViewImageCount = (TextView) convertView.findViewById(R.id.receipt_image_count);
        if (_receipts != null && _receipts.size() > 0) {
            Receipt a = _receipts.get(position);
            textViewName.setText("ID: " + a.getID());
            textViewStatus.setText("Status: " + a.getStatus().toString());
            textViewCreateTime.setText("Created Time: " + a.getCreatedTime());
            textViewUpdateTime.setText("Updated Time: " + a.getUpdatedTime());
            textViewImageCount.setText("Image Count: " + a.getImageCount());
            if (!a.getStatus().equals(Receipt.ReceiptStatusType.INVALID)) {
                textViewInvalidCode.setVisibility(View.GONE);
                textViewInvalidReason.setVisibility(View.GONE);
            } else {
                textViewInvalidCode.setText("Invalid Code: " + a.getInvalidCode());
                textViewInvalidReason.setText("Invalid Reason: " + a.getInvalidReason());
                textViewInvalidCode.setVisibility(View.VISIBLE);
                textViewInvalidReason.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }
}