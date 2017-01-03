/*
* Copyright (c) 2016 SessionM. All rights reserved.
*/
package com.sessionm.smp.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.referral.ReferralsListener;
import com.sessionm.api.referral.ReferralsManager;
import com.sessionm.api.referral.data.Referral;
import com.sessionm.api.referral.data.ReferralError;
import com.sessionm.api.referral.data.ReferralRequest;
import com.sessionm.smp.R;
import com.sessionm.smp.controller.ReferralsListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReferralsFragment extends BaseScrollAndRefreshFragment {
    List<Referral> _referrals = new ArrayList<>();
    private SwipeRefreshLayout _swipeRefreshLayout;
    private ReferralsListAdapter _referralsListAdapter;
    ReferralsManager _referralsManager;
    private RecyclerView _recyclerView;

    public static ReferralsFragment newInstance() {
        ReferralsFragment f = new ReferralsFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

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
        _recyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
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

    public void popUpCreateReferralDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_create_referral, null);

        final EditText refereeEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_referee);
        final EditText emailEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_email);
        final EditText phoneNumberEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_phone_number);
        final EditText originEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_origin);
        final EditText sourceEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_source);
        final EditText clientDataEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_client_data);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNeutralButton("Create Random", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SessionM.getInstance().getReferralsManager().sendReferrals(createRandomReferralRequest());
            }
        });
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SessionM.getInstance().getReferralsManager().sendReferrals(
                        createReferralRequest(refereeEdittext.getText().toString(),
                                emailEdittext.getText().toString(),
                                phoneNumberEdittext.getText().toString(),
                                originEdittext.getText().toString(),
                                sourceEdittext.getText().toString(),
                                clientDataEdittext.getText().toString()
                        ));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setTitle("Refer A Friend");

        AlertDialog dialog = builder.create();

        dialog.setView(dialogLayout);
        dialog.show();
    }


    private ReferralRequest createRandomReferralRequest() {
        Referral referral1 = ReferralsManager.referralBuilder().referee("" + System.currentTimeMillis()).email(System.currentTimeMillis() + "2@test.com")
                .phoneNumber("5081111111").origin("hello").clientData("test").source("ete").build();
        Referral referral2 = ReferralsManager.referralBuilder().referee("" + System.currentTimeMillis() + 1).email(System.currentTimeMillis() + "@test.com")
                .phoneNumber("6171111111").origin("hi").clientData("sss").source("eve").build();
        List<Referral> referrals = new ArrayList<>();
        referrals.add(referral1);
        referrals.add(referral2);
        return ReferralsManager.referralRequestBuilder().referrer("randomUser").referrals(referrals).build();
    }

    private ReferralRequest createReferralRequest(String referee, String email, String phoneNumber, String origin, String source, String clientData) {
        Referral referral1 = ReferralsManager.referralBuilder().referee(referee).email(email)
                .phoneNumber(phoneNumber).origin(origin).clientData(clientData).source(source).build();
        List<Referral> referrals = new ArrayList<>();
        referrals.add(referral1);
        return ReferralsManager.referralRequestBuilder().referrer("currentUser").referrals(referrals).build();
    }
}


