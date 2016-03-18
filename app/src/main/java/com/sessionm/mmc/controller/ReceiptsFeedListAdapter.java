/*
 * Copyright (c) 2015 SessionM. All rights reserved.
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

    private final Context context;
    private final List<Receipt> receipts;
    private LayoutInflater inflater;

    public ReceiptsFeedListAdapter(Context context, List<Receipt> receipts) {
        this.context = context;
        this.receipts = receipts;
    }

    @Override
    public int getCount() {
        return receipts.size();
    }

    @Override
    public Object getItem(int position) {
        return receipts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.receipt_row, parent, false);

        TextView textView_name = (TextView) convertView.findViewById(R.id.receiptName);
        TextView textView_status = (TextView) convertView.findViewById(R.id.receiptStatus);
        TextView textView_invalid_code = (TextView) convertView.findViewById(R.id.receiptInvalidCode);
        TextView textView_invalid_reason = (TextView) convertView.findViewById(R.id.receiptInvalidReason);
        TextView textView_upload_time = (TextView) convertView.findViewById(R.id.receiptUploadTime);
        if (receipts != null && receipts.size() > 0) {
            Receipt a = receipts.get(position);
            textView_name.setText("ID: " + a.getID());
            textView_status.setText("Status: " + a.getStatus().toString());
            textView_upload_time.setText("Uploaded Time: " + a.getCreatedTime());
            if (!a.getStatus().equals(Receipt.ReceiptStatusType.INVALID)) {
                textView_invalid_code.setVisibility(View.GONE);
                textView_invalid_reason.setVisibility(View.GONE);
            } else {
                textView_invalid_code.setText("Invalid Code: " + a.getInvalidCode());
                textView_invalid_reason.setText("Invalid Reason: " + a.getInvalidReason());
                textView_invalid_code.setVisibility(View.VISIBLE);
                textView_invalid_reason.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }
}