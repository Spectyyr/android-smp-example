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

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.referral.ReferralsListener;
import com.sessionm.api.referral.ReferralsManager;
import com.sessionm.api.referral.data.Referral;
import com.sessionm.api.referral.data.ReferralError;

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

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.referral_swipelayout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _referralsManager = SessionM.getInstance().getReferralsManager();
        _referrals = new ArrayList<>(_referralsManager.getReferrals());

        _recyclerView = (RecyclerView) rootView.findViewById(R.id.referrals_list);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _referralsListAdapter = new ReferralsListAdapter(this, _referrals);
        _recyclerView.setAdapter(_referralsListAdapter);

        return rootView;
    }

    ReferralsListener _referralsListener = new ReferralsListener() {
        @Override
        public void onReferralsFetched(List<Referral> referrals) {
            refreshList(referrals);
        }

        @Override
        public void onReferralsSent(List<Referral> list, List<ReferralError> list1, SessionMError sessionMError) {
            Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
            _referralsManager.fetchReferrals();
        }

        @Override
        public void onFailure(SessionMError error) {
            _swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            refreshList(_referralsManager.getReferrals());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        _referralsManager.setListener(_referralsListener);
        _referralsManager.fetchReferrals();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        _referralsManager.fetchReferrals();
    }

    private void refreshList(List<Referral> referrals) {
        _swipeRefreshLayout.setRefreshing(false);
        if (_referrals == null) {
            _referrals = new ArrayList<>();
        } else {
            _referrals.clear();
        }
        _referrals.addAll(referrals);
        _referralsListAdapter.notifyDataSetChanged();
    }


}


