/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sessionm.api.transaction.data.Transaction;
import com.sessionm.mmc.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//Adapter class to draw Transaction List
public class TransactionsFeedListAdapter extends BaseAdapter {

    private final Context _context;
    private final List<Transaction> _transactions = new ArrayList<>();
    private LayoutInflater _inflater;

    public TransactionsFeedListAdapter(Context context) {
        _context = context;
    }

    public void addTransactions(List<Transaction> transactions, boolean clear) {
        if (clear) {
            _transactions.clear();
        }
        _transactions.addAll(transactions);
        Log.d("TAG", "Trans: " + transactions.size() + ", _Trans: " + _transactions.size());

        Collections.sort(_transactions, new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                String ldate;
                String rdate;
                ldate = ((Transaction) lhs).getDate().replace("T", " ").replaceAll("[.][0-9]*Z", "");
                rdate = ((Transaction) rhs).getDate().replace("T", " ").replaceAll("[.][0-9]+Z$", "");
                return rdate.compareTo(ldate);
            }
        });
        notifyDataSetChanged();
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
            holder = new ViewHolder();
            convertView = _inflater.inflate(R.layout.transaction_row, parent, false);
            holder.textView_balance = (TextView) convertView.findViewById(R.id.transaction_balance);
            holder.textView_date = (TextView) convertView.findViewById(R.id.transaction_date);
            holder.textView_description = (TextView) convertView.findViewById(R.id.transaction_description);
            holder.textView_points = (TextView) convertView.findViewById(R.id.transaction_points);
            holder.textView_record_id = (TextView) convertView.findViewById(R.id.transaction_record_id);
            holder.textView_transaction = (TextView) convertView.findViewById(R.id.transaction_transaction);
            holder.textView_source = (TextView) convertView.findViewById(R.id.transaction_source);
            holder.textView_type = (TextView) convertView.findViewById(R.id.transaction_type);
            holder.textView_record_model_id = (TextView) convertView.findViewById(R.id.record_model_id);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Transaction transaction = (Transaction) _transactions.get(position);
        holder.textView_balance.setText("Balance: " + transaction.getBalance());
        holder.textView_date.setText("Date: " + transaction.getDate());
        holder.textView_description.setText("Description: " + transaction.getDescription());
        holder.textView_points.setText("Points: " + transaction.getPoints());
        holder.textView_record_id.setText("Record ID: " + transaction.getRecordID());
        holder.textView_transaction.setText("Transaction: " + transaction.getTransaction());
        holder.textView_source.setText("Source: " + transaction.getSource());
        holder.textView_type.setText("Type: " + transaction.getType());
        holder.textView_record_model_id.setText("Ref: " + transaction.getRecordModelID());

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
        TextView textView_record_model_id;
    }
}