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
        ViewHolder holder;
        if (convertView == null) {
            if (_inflater == null)
                _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = _inflater.inflate(R.layout.transaction_row, parent, false);
            holder = new ViewHolder();
            holder.textView_balance = (TextView) convertView.findViewById(R.id.transaction_balance);
            holder.textView_date = (TextView) convertView.findViewById(R.id.transaction_date);
            holder.textView_description = (TextView) convertView.findViewById(R.id.transaction_description);
            holder.textView_points = (TextView) convertView.findViewById(R.id.transaction_points);
            holder.textView_record_id = (TextView) convertView.findViewById(R.id.transaction_record_id);
            holder.textView_transaction = (TextView) convertView.findViewById(R.id.transaction_transaction);
            holder.textView_source = (TextView) convertView.findViewById(R.id.transaction_source);
            holder.textView_type = (TextView) convertView.findViewById(R.id.transaction_type);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Transaction transaction = _transactions.get(position);
        holder.textView_balance.setText("Balance: " + transaction.getBalance());
        holder.textView_date.setText("Date: " + transaction.getDate());
        holder.textView_description.setText("Description: " + transaction.getDescription());
        holder.textView_points.setText("Points: " + transaction.getPoints());
        holder.textView_record_id.setText("Record ID: " + transaction.getRecordID());
        holder.textView_transaction.setText("Transaction: " + transaction.getTransaction());
        holder.textView_source.setText("Source: " + transaction.getSource());
        holder.textView_type.setText("Type: " + transaction.getType());

        return convertView;
    }

    private static class ViewHolder {
        TextView textView_balance;
        TextView textView_date;
        TextView textView_description;
        TextView textView_points;
        TextView textView_record_id;
        TextView textView_transaction;
        TextView textView_source;
        TextView textView_type;
    }
}