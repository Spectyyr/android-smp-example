/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */
package com.sessionm.smp_referrals;

import android.os.Bundle;
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
import com.sessionm.referral.api.ReferralsManager;
import com.sessionm.referral.api.data.Referral;

import java.util.ArrayList;
import java.util.List;

public class ReferralsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    List<Referral> _referrals = new ArrayList<>();
    private SwipeRefreshLayout _swipeRefreshLayout;
    private ReferralsListAdapter _referralsListAdapter;
    ReferralsManager _referralsManager;
    private RecyclerView _recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_referrals, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = rootView.findViewById(R.id.referral_swipelayout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _referralsManager = ReferralsManager.getInstance();
        _referrals = new ArrayList<>(_referralsManager.getReferrals());

        _recyclerView = rootView.findViewById(R.id.referrals_list);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _referralsListAdapter = new ReferralsListAdapter(_referrals);
        _recyclerView.setAdapter(_referralsListAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchReferrals();
    }

    @Override
    public void onRefresh() {
        fetchReferrals();
    }

    private void fetchReferrals() {
        _referralsManager.fetchReferrals(new ReferralsManager.OnReferralsFetchedListener() {
            @Override
            public void onFetched(List<Referral> list, SessionMError sessionMError) {
                if (sessionMError != null) {
                    Toast.makeText(getActivity(), sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                }
                refreshList(list);
            }
        });
    }

    private void refreshList(List<Referral> referrals) {
        _swipeRefreshLayout.setRefreshing(false);
        if (referrals == null)
            return;
        if (_referrals == null) {
            _referrals = new ArrayList<>();
        } else {
            _referrals.clear();
        }
        _referrals.addAll(referrals);
        _referralsListAdapter.notifyDataSetChanged();
    }
}


