/*
 * Copyright (c) 2015 SessionM. All rights reserved.
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

    private SwipeRefreshLayout swipeRefreshLayout;
    private ObservableListView listView;
    private CampaignsFeedListAdapter listAdapter;
    //private List of SessionM Message
    private List<FeedMessage> messages;
    //Offline textview
    TextView offlinePromoTextView;

    private CampaignsManager _campaignsManager;

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

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        listView = (ObservableListView) rootView.findViewById(R.id.message_feed_list);
        _campaignsManager = SessionM.getInstance().getCampaignsManager();
        messages = _campaignsManager.getFeedMessages();
        offlinePromoTextView = (TextView) rootView.findViewById(R.id.promotion_offline);
        if (messages != null) {
            listAdapter = new CampaignsFeedListAdapter(getActivity(), messages);
            listView.setAdapter(listAdapter);
        }
        listView.setScrollViewCallbacks(this);
        updateOfflineLayout();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _campaignsManager.fetchFeedMessages();
    }

    CampaignsListener campaignsListener = new CampaignsListener() {
        @Override
        public void onFeedMessagesFetched(List<FeedMessage> list) {
            swipeRefreshLayout.setRefreshing(false);
            if (messages == null) {
                messages = new ArrayList<>();
            } else {
                messages.clear();
            }
            messages.addAll(list);
            if (listAdapter == null) {
                listAdapter = new CampaignsFeedListAdapter(getActivity(), messages);
                listView.setAdapter(listAdapter);
            }
            listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(SessionMError error) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        _campaignsManager.setListener(campaignsListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        _campaignsManager.setListener(null);
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
            listView.setVisibility(View.VISIBLE);
            offlinePromoTextView.setVisibility(View.GONE);
        }
        //What to do when a Session does not start, handling international users, no WI-Fi connection
        if (state.equals(SessionM.State.STARTED_OFFLINE)) {
            listView.setVisibility(View.GONE);
            offlinePromoTextView.setVisibility(View.VISIBLE);
        }
    }
}