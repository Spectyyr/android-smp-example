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

import com.sessionm.api.transaction.data.Transaction;
import com.sessionm.mmc.R;

import java.util.List;

//Adapter class to draw Transaction List
public class TransactionsFeedListAdapter extends BaseAdapter {

    private final Context _context;
    private final List<Transaction> _transactions;
    private LayoutInflater _inflater;

    public TransactionsFeedListAdapter(Context context, List<Transaction> transactions) {
        _context = context;
        _transactions = transactions;
    }

    @Override
    public int getCount() {
        return _transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return _transactions.get(position);
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
            convertView = _inflater.inflate(R.layout.transaction_row, parent, false);
        TextView textView_balance = (TextView) convertView.findViewById(R.id.transaction_balance);
        TextView textView_date = (TextView) convertView.findViewById(R.id.transaction_date);
        TextView textView_description = (TextView) convertView.findViewById(R.id.transaction_description);
        TextView textView_points = (TextView) convertView.findViewById(R.id.transaction_points);
        TextView textView_record_id = (TextView) convertView.findViewById(R.id.transaction_record_id);
        TextView textView_transaction = (TextView) convertView.findViewById(R.id.transaction_transaction);
        TextView textView_source = (TextView) convertView.findViewById(R.id.transaction_source);
        TextView textView_type = (TextView) convertView.findViewById(R.id.transaction_type);
        final Transaction transaction = _transactions.get(position);
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