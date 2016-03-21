/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.campaign.CampaignsListener;
import com.sessionm.api.campaign.CampaignsManager;
import com.sessionm.api.message.feed.data.FeedMessage;
import com.sessionm.mmc.R;
import com.sessionm.mmc.controller.CampaignsFeedListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment of SessionM List of Message
 */
public class CampaignsFragment extends BaseScrollAndRefreshFragment {
    private static final String TAG = "FeedListActivity";

    private SwipeRefreshLayout _swipeRefreshLayout;
    private ObservableListView _listView;
    private CampaignsFeedListAdapter _listAdapter;
    //private List of SessionM Message
    private List<FeedMessage> _messages;
    //Offline textview
    TextView _offlinePromoTextView;

    private CampaignsManager _campaignsManager = new CampaignsManager();

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

        _listView = (ObservableListView) rootView.findViewById(R.id.message_feed_list);
        _campaignsManager.setListener(_campaignsListener);
        _messages = _campaignsManager.getFeedMessages();
        _offlinePromoTextView = (TextView) rootView.findViewById(R.id.promotion_offline);
        if (_messages != null) {
            _listAdapter = new CampaignsFeedListAdapter(getActivity(), _messages);
            _listView.setAdapter(_listAdapter);
        }
        _listView.setScrollViewCallbacks(this);
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
            if (_listAdapter == null) {
                _listAdapter = new CampaignsFeedListAdapter(getActivity(), _messages);
                _listView.setAdapter(_listAdapter);
            }
            _listAdapter.notifyDataSetChanged();
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
            _listView.setVisibility(View.VISIBLE);
            _offlinePromoTextView.setVisibility(View.GONE);
        }
        //What to do when a Session does not start, handling international users, no WI-Fi connection
        if (state.equals(SessionM.State.STARTED_OFFLINE)) {
            _listView.setVisibility(View.GONE);
            _offlinePromoTextView.setVisibility(View.VISIBLE);
        }
    }
}