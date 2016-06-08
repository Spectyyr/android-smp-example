/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.loyaltycard.LoyaltyCardsListener;
import com.sessionm.api.loyaltycard.LoyaltyCardsManager;
import com.sessionm.api.loyaltycard.data.LoyaltyCard;
import com.sessionm.api.loyaltycard.data.LoyaltyCardTransaction;
import com.sessionm.api.loyaltycard.data.Retailer;
import com.sessionm.api.reward.data.offer.Offer;
import com.sessionm.mmc.R;
import com.sessionm.mmc.controller.LoyaltyCardsListAdapter;
import com.sessionm.mmc.controller.RewardsFeedListAdapter;

import java.util.ArrayList;
import java.util.List;

public class LoyaltyFragment extends BaseScrollAndRefreshFragment {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private ObservableListView _listView;
    private List<LoyaltyCard> _cards;
    private LoyaltyCardsManager _loyaltyManager;
    private LoyaltyCardsListAdapter _listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loyalty, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _listView = (ObservableListView) rootView.findViewById(R.id.card_list);
        _loyaltyManager = SessionM.getInstance().getLoyaltyCardsManager();
        _cards = new ArrayList<>(_loyaltyManager.getLoyaltyCards());
        _listAdapter = new LoyaltyCardsListAdapter(getActivity(), _cards);
        _listView.setAdapter(_listAdapter);

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final LoyaltyCard row = (LoyaltyCard) _listAdapter.getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(String.format("Unlink Card #: %s from %s", row.getCardNumber(), row.getRetailer().getName()))
                        .setTitle("Unlink Loyalty Card");
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        _loyaltyManager.unlinkLoyaltyCard(row.getID());
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(), "You didn't unlink it", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        _listView.setScrollViewCallbacks(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    LoyaltyCardsListener _loyaltyListener = new LoyaltyCardsListener() {
        @Override public void onRetailersFetched(List<Retailer> retailers) { }
        @Override public void onLoyaltyCardLinked(String cardNumber) { }
        @Override public void onLoyaltyCardUnlinked() {
            _loyaltyManager.fetchLinkedCards();
        }

        @Override public void onLoyaltyCardsFetched(List<LoyaltyCard>cards) {
            _swipeRefreshLayout.setRefreshing(false);
            _cards.clear();
            if (cards == null) {
                cards = new ArrayList<>();
            }
            _cards.addAll(cards);
            if (_listAdapter == null) {
                _listAdapter = new LoyaltyCardsListAdapter(getActivity(), _cards);
                _listView.setAdapter(_listAdapter);
            }
            _listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoyaltyCardTransactionsFetched(List<LoyaltyCardTransaction> list) {

        }

        @Override
        public void onFailure(SessionMError sessionMError) {
            _swipeRefreshLayout.setRefreshing(false);
            _loyaltyManager.fetchLinkedCards();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        _loyaltyManager.setListener(_loyaltyListener);
        _loyaltyManager.fetchLinkedCards();
    }

    @Override
    public void onPause() {
        super.onPause();
        _loyaltyManager.setListener(null);
    }

    @Override
    public void onRefresh() {
        _loyaltyManager.fetchLinkedCards();
    }

    public static LoyaltyFragment newInstance() {
        LoyaltyFragment f = new LoyaltyFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

}
