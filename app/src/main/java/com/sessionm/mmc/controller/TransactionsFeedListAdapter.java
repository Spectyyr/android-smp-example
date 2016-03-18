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

import com.sessionm.api.transaction.data.Transaction;
import com.sessionm.mmc.R;

import java.util.List;

//Adapter class to draw Transaction List
public class TransactionsFeedListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Transaction> transactions;
    private LayoutInflater inflater;

    public TransactionsFeedListAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
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
            convertView = inflater.inflate(R.layout.transaction_row, parent, false);
        TextView textView_balance = (TextView) convertView.findViewById(R.id.transaction_balance);
        TextView textView_date = (TextView) convertView.findViewById(R.id.transaction_date);
        TextView textView_description = (TextView) convertView.findViewById(R.id.transaction_description);
        TextView textView_points = (TextView) convertView.findViewById(R.id.transaction_points);
        TextView textView_record_id = (TextView) convertView.findViewById(R.id.transaction_record_id);
        TextView textView_transaction = (TextView) convertView.findViewById(R.id.transaction_transaction);
        TextView textView_source = (TextView) convertView.findViewById(R.id.transaction_source);
        TextView textView_type = (TextView) convertView.findViewById(R.id.transaction_type);
        final Transaction transaction = transactions.get(position);
        textView_balance.setText("Balance: " + transaction.getBalance());
        textView_date.setText("Date: " + transaction.getDate());
        textView_description.setText("Description: " + transaction.getDescription());
        textView_points.setText("Points: " + transaction.getPoints());
        textView_record_id.setText("Record ID: " + transaction.getRecordID());
        textView_transaction.setText("Transaction: " + transaction.getTransaction());
        textView_source.setText("Source: " + transaction.getSource());
        textView_type.setText("Type: " + transaction.getType());

        return convertView;
    }
}