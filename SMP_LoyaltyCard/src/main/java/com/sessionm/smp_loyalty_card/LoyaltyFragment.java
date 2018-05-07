/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_loyalty_card;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sessionm.core.api.SessionMError;
import com.sessionm.loyaltycard.api.LoyaltyCardsManager;
import com.sessionm.loyaltycard.api.data.LoyaltyCard;

import java.util.ArrayList;
import java.util.List;

public class LoyaltyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private List<LoyaltyCard> _cards;
    private LoyaltyCardsManager _loyaltyManager = LoyaltyCardsManager.getInstance();
    private LoyaltyCardsRecAdapter _listAdapter;
    private RecyclerView _recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loyalty, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _cards = new ArrayList<>(_loyaltyManager.getLoyaltyCards());

        _recyclerView = rootView.findViewById(R.id.card_list);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _listAdapter = new LoyaltyCardsRecAdapter(this, _cards);
        _recyclerView.setAdapter(_listAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchLinkedCards();
    }

    public void fetchLinkedCards() {
        _loyaltyManager.fetchLinkedCards(new LoyaltyCardsManager.OnLoyaltyCardsFetchedListener() {
            @Override
            public void onFetched(List<LoyaltyCard> list, SessionMError sessionMError) {
                if (sessionMError != null) {
                    Toast.makeText(getActivity(), sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                    refreshList(_loyaltyManager.getLoyaltyCards());
                } else {
                    refreshList(list);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        fetchLinkedCards();
    }

    private void refreshList(List<LoyaltyCard> cards) {
        _swipeRefreshLayout.setRefreshing(false);
        _cards.clear();
        if (cards == null) {
            cards = new ArrayList<>();
        }
        _cards.addAll(cards);
        _listAdapter.notifyDataSetChanged();
    }
}
