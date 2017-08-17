/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.loyaltycard.LoyaltyCardsListener;
import com.sessionm.api.loyaltycard.LoyaltyCardsManager;
import com.sessionm.api.loyaltycard.data.LoyaltyCard;
import com.sessionm.api.loyaltycard.data.LoyaltyCardTransaction;
import com.sessionm.api.loyaltycard.data.Retailer;
import com.sessionm.api.receipt.ReceiptsListener;
import com.sessionm.api.receipt.ReceiptsManager;
import com.sessionm.api.receipt.data.Receipt;
import com.sessionm.api.receipt.data.ReceiptResult;
import com.sessionm.api.transaction.data.Transaction;
import com.sessionm.smp.R;
import com.sessionm.smp.view.TransactionsFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

//Adapter class to draw Transaction List
public class TransactionsFeedListAdapter extends RecyclerView.Adapter<TransactionsFeedListAdapter.TransactionsViewHolder> {

    private final List<Transaction> _transactions = new ArrayList<>();
    private TransactionsFragment _fragment;

    public TransactionsFeedListAdapter(TransactionsFragment fragment) {
        _fragment = fragment;
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
        holder.descriptionTextView.setText("Description: " + transaction.getDescription());
        holder.pointsTextView.setText("Points: " + transaction.getPoints());
        holder.recordIDTextView.setText("Record ID: " + transaction.getRecordID());
        holder.transactionTextView.setText("Transaction: " + transaction.getTransaction());
        holder.sourceTextView.setText("Source: " + transaction.getSource());
        holder.typeTextView.setText("Type: " + transaction.getType());
        holder.recordModelIDTextView.setText("Ref: " + transaction.getRecordModelID());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new ShowDetails(_fragment.getActivity())).showDetail(transaction);
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
            balanceTextView = (TextView) v.findViewById(R.id.transaction_balance);
            dateTextView = (TextView) v.findViewById(R.id.transaction_date);
            descriptionTextView = (TextView) v.findViewById(R.id.transaction_description);
            pointsTextView = (TextView) v.findViewById(R.id.transaction_points);
            recordIDTextView = (TextView) v.findViewById(R.id.transaction_record_id);
            transactionTextView = (TextView) v.findViewById(R.id.transaction_transaction);
            sourceTextView = (TextView) v.findViewById(R.id.transaction_source);
            typeTextView = (TextView) v.findViewById(R.id.transaction_type);
            recordModelIDTextView = (TextView) v.findViewById(R.id.record_model_id);
        }
    }

    private static class ShowDetails {
        private Activity _activity;
        private ListView _lv;
        private ArrayAdapter<String> _la;
        private LoyaltyCardsManager _lclManager;
        private String _transactionID;
        private String _resultID;
        private ReceiptsManager _receiptManager;

        public ShowDetails(Activity activity) {
            this._activity = activity;
        }

        void showDetail(Transaction transaction) {
            if (transaction.getType() == Transaction.TransactionPointsType.RECEIPT) {
                showDialog();
                getReceiptsWithResult(transaction.getRecordModelID());
            } else if (transaction.getType() == Transaction.TransactionPointsType.LOYALTY_CARD) {
                getLCLTransactionsWithID(transaction.getRecordModelID());
                showDialog();
            }
        }

        private void showDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
            builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("TAG", "button dismiss");
                }
            });
            AlertDialog dialog = builder.create();
            LayoutInflater inflater = _activity.getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.transaction_linked, null);

            _lv = (ListView) dialogLayout.findViewById(R.id.transaction_details);
            _la = new ArrayAdapter<>(_activity, android.R.layout.simple_list_item_1, new LinkedList<String>());
            _lv.setAdapter(_la);
            _lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("TAG", "Item Clicked");
                }
            });

            dialog.setView(dialogLayout);
            dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.show();
        }

        private void getLCLTransactionsWithID(String transactionID) {
            _transactionID = transactionID;
            _lclManager = SessionM.getInstance().getLoyaltyCardsManager();
            _lclManager.setListener(_lclListener);
            _lclManager.fetchCardTransactions(1000, 1);
        }

        private void getReceiptsWithResult(String resultID) {
            _resultID = resultID;
            _receiptManager = SessionM.getInstance().getReceiptsManager();
            _receiptManager.setListener(_receiptListener);
            _receiptManager.fetchReceipts(100, 1);
        }

        private ReceiptsListener _receiptListener = new ReceiptsListener() {
            @Override public void onReceiptUploaded(Receipt receipt) { }
            @Override public void onProgress(Receipt receipt) { }

            @Override public void onReceiptsFetched(List<Receipt> receipts) {
                Log.d("TAG", "Rs: " + receipts.size());
                List<String>result = new LinkedList<>();
                for (Receipt lct : receipts) {
                    for (ReceiptResult rr : lct.getResults()) {
                        if (rr.getID().equals(_resultID)) {
                            result.add(String.format("ReceiptID: %s, Status: %s", lct.getID(), lct.getStatus()));
                            result.add(String.format("Result ID: %s", rr.getID()));
                            result.add(String.format("Description: %s", rr.getDescription()));
                            result.add(String.format("Name: %s", rr.getName()));
                            result.add(String.format("Price: %.2f", rr.getPrice()));
                            result.add(String.format("Quantity: %d", rr.getQuantity()));
                            _la.clear();
                            _la.addAll(result);
                            break;
                        }
                    }
                }
            }
            @Override public void onFailure(SessionMError sessionMError) { }
        };

        private LoyaltyCardsListener _lclListener = new LoyaltyCardsListener() {
            @Override public void onRetailersFetched(List<Retailer> list) { }
            @Override public void onLoyaltyCardLinked(String s) { }
            @Override public void onLoyaltyCardUnlinked() { }
            @Override public void onLoyaltyCardsFetched(List<LoyaltyCard> list) { }

            @Override
            public void onLoyaltyCardTransactionsFetched(List<LoyaltyCardTransaction> transactions) {
                Log.d("TAG", "Ts: " + transactions.size());
                List<String>result = new LinkedList<>();
                for (LoyaltyCardTransaction lct : transactions) {
                    if (lct.getID().equals(_transactionID)) {
                        result.add(String.format("Description: %s", lct.getDescription()));
                        result.add(String.format("Name: %s", lct.getName()));
                        result.add(String.format("Price: %.2f", lct.getPrice()));
                        result.add(String.format("Quantity: %d", lct.getQuantity()));
                        result.add(String.format("Points: %d", lct.getPoints()));
                        result.add(String.format("Date: %s", lct.getDate()));
                        _la.clear();
                        _la.addAll(result);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(SessionMError sessionMError) {

            }
        };
    }
}