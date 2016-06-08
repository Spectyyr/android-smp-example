/*
* Copyright (c) 2016 SessionM. All rights reserved.
*/
package com.sessionm.mmc.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
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
import com.sessionm.api.transaction.TransactionsListener;
import com.sessionm.api.transaction.TransactionsManager;
import com.sessionm.api.transaction.data.Transaction;
import com.sessionm.mmc.R;
import com.sessionm.mmc.controller.TransactionsFeedListAdapter;

import java.util.LinkedList;
import java.util.List;

public class TransactionsFragment extends BaseScrollAndRefreshFragment {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private ObservableListView _listView;
    private TransactionsFeedListAdapter _listAdapter;

    TransactionsManager _transactionsManager = SessionM.getInstance().getTransactionsManager();
    private boolean noTransactions;

    public static TransactionsFragment newInstance() {
        TransactionsFragment f = new TransactionsFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _listView = (ObservableListView) rootView.findViewById(R.id.transactions_feed_list);
        _listAdapter = new TransactionsFeedListAdapter(getActivity());
        _listView.setAdapter(_listAdapter);
        _listView.setScrollViewCallbacks(this);
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                (new showDetails(getActivity())).showDetail((Transaction)(_listAdapter.getItem(position)));
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noTransactions = true;
        fetchAllTransactions();
    }

    TransactionsListener _transactionListener = new TransactionsListener() {
        @Override
        public void onTransactionsFetched(List<Transaction> transactions, boolean hasMore) {
            if (_swipeRefreshLayout.isRefreshing()) {
                _swipeRefreshLayout.setRefreshing(false);
            }

            _listAdapter.addTransactions(transactions, noTransactions);
            noTransactions = false;

            if (hasMore)
                _transactionsManager.fetchMoreTransactions();
        }

        @Override
        public void onFailure(SessionMError error) {
            if (_swipeRefreshLayout.isRefreshing()) {
                _swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getActivity(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        fetchAllTransactions();
    }

    private void fetchAllTransactions() {
        noTransactions = true;

        _transactionsManager.setListener(_transactionListener);
        _transactionsManager.fetchTransactions(null, null, 50);
    }

    private static class showDetails {
        private Activity _activity;
        private ListView _lv;
        private ArrayAdapter<String> _la;
        private LoyaltyCardsManager _lclManager;
        private String _transactionID;
        private String _resultID;
        private ReceiptsManager _receiptManager;

        public showDetails(Activity activity) {
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
            _receiptManager = SessionM.getInstance().getReceiptManager();
            _receiptManager.setListener(_receiptListener);
            _receiptManager.fetchReceipts();
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
