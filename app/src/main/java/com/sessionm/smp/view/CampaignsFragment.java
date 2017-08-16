/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.campaign.CampaignsListener;
import com.sessionm.api.campaign.CampaignsManager;
import com.sessionm.api.campaign.data.FeedMessage;
import com.sessionm.smp.R;
import com.sessionm.smp.controller.CampaignsRecAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment of SessionM List of Message
 */
public class CampaignsFragment extends BaseScrollAndRefreshFragment {
    private static final String TAG = "FeedListActivity";

    private SwipeRefreshLayout _swipeRefreshLayout;
    private CampaignsRecAdapter _campaignsRecAdapter;
    //private List of SessionM Message
    private List<FeedMessage> _messages;
    //Offline textview
    TextView _offlinePromoTextView;
    private RecyclerView _recyclerView;

    OnDeepLinkTappedListener onDeepLinkTappedListener;

    private CampaignsManager _campaignsManager = SessionM.getInstance().getCampaignsManager();

    public static CampaignsFragment newInstance() {
        CampaignsFragment f = new CampaignsFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campaigns, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);


        _campaignsManager.setListener(_campaignsListener);
        _messages = new ArrayList<>(_campaignsManager.getFeedMessages());
        _offlinePromoTextView = (TextView) rootView.findViewById(R.id.promotion_offline);

        _recyclerView = (RecyclerView) rootView.findViewById(R.id.message_feed_list);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _recyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        _campaignsRecAdapter = new CampaignsRecAdapter(this, _messages);
        _recyclerView.setAdapter(_campaignsRecAdapter);
        updateOfflineLayout();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _campaignsManager.fetchFeedMessages();
    }

    CampaignsListener _campaignsListener = new CampaignsListener() {
        @Override
        public void onFeedMessagesFetched(List<FeedMessage> list) {
            _swipeRefreshLayout.setRefreshing(false);
            if (_messages == null) {
                _messages = new ArrayList<>();
            } else {
                _messages.clear();
            }
            _messages.addAll(list);
            _campaignsRecAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(SessionMError error) {
            _swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        _campaignsManager.setListener(_campaignsListener);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        _campaignsManager.fetchFeedMessages();
    }

    public void updateOfflineLayout() {
        SessionM.State state = SessionM.getInstance().getSessionState();
        //Check is session is started
        if (state.equals(SessionM.State.STARTED_ONLINE)) {
            //Do whatever you want
            _recyclerView.setVisibility(View.VISIBLE);
            _offlinePromoTextView.setVisibility(View.GONE);
        }
        //What to do when a Session does not start, handling international users, no WI-Fi connection
        if (state.equals(SessionM.State.STARTED_OFFLINE)) {
            _recyclerView.setVisibility(View.GONE);
            _offlinePromoTextView.setVisibility(View.VISIBLE);
        }
    }

    public void onItemTapped(FeedMessage.MessageActionType actionType, String actionURL) {
        onDeepLinkTappedListener.onDeepLinkTapped(actionType, actionURL);
    }

    //On deep link listener to talk up to activity
    public interface OnDeepLinkTappedListener {
        void onDeepLinkTapped(FeedMessage.MessageActionType actionType, String actionURL);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onDeepLinkTappedListener = (OnDeepLinkTappedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDeepLinkTappedListener");
        }
    }
}