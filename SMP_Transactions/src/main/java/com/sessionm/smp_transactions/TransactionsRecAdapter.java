/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_transactions;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sessionm.transaction.api.data.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//Adapter class to draw Transaction List
public class TransactionsRecAdapter extends RecyclerView.Adapter<TransactionsRecAdapter.TransactionsViewHolder> {

    private final List<Transaction> _transactions = new ArrayList<>();

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
    public TransactionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.feed_item_transaction, parent, false);

        return new TransactionsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TransactionsViewHolder holder, int position) {
        final Transaction transaction = _transactions.get(position);
        holder.balanceTextView.setText("Balance: " + transaction.getBalance());
        holder.dateTextView.setText("Date: " + transaction.getDate());
        holder.descriptionTextView.setText("Description: " + transaction.getDetails());
        holder.pointsTextView.setText("Points: " + transaction.getPoints());
        holder.recordIDTextView.setText("Record ID: " + transaction.getRecordID());
        holder.transactionTextView.setText("Transaction: " + transaction.getTransaction());
        holder.sourceTextView.setText("Source: " + transaction.getSource());
        holder.typeTextView.setText("Type: " + transaction.getType());
        holder.recordModelIDTextView.setText("Ref: " + transaction.getRecordModelID());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _transactions.size();
    }

    public static class TransactionsViewHolder extends RecyclerView.ViewHolder {
        TextView balanceTextView;
        TextView dateTextView;
        TextView descriptionTextView;
        TextView pointsTextView;
        TextView recordIDTextView;
        TextView transactionTextView;
        TextView sourceTextView;
        TextView typeTextView;
        TextView recordModelIDTextView;


        public TransactionsViewHolder(View v) {
            super(v);
            balanceTextView = v.findViewById(R.id.transaction_balance);
            dateTextView = v.findViewById(R.id.transaction_date);
            descriptionTextView = v.findViewById(R.id.transaction_description);
            pointsTextView = v.findViewById(R.id.transaction_points);
            recordIDTextView = v.findViewById(R.id.transaction_record_id);
            transactionTextView = v.findViewById(R.id.transaction_transaction);
            sourceTextView = v.findViewById(R.id.transaction_source);
            typeTextView = v.findViewById(R.id.transaction_type);
            recordModelIDTextView = v.findViewById(R.id.record_model_id);
        }
    }
}