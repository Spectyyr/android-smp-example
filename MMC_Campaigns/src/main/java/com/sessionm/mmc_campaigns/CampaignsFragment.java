/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc_campaigns;

import android.content.Context;
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

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.campaign.CampaignsListener;
import com.sessionm.api.campaign.CampaignsManager;
import com.sessionm.api.message.data.Message;
import com.sessionm.api.message.feed.data.FeedMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment of SessionM List of Message
 */
public class CampaignsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "FeedListActivity";

    private SwipeRefreshLayout _swipeRefreshLayout;
    private CampaignsRecAdapter _campaignsRecAdapter;
    //private List of SessionM Message
    private List<FeedMessage> _messages;
    private RecyclerView _recyclerView;

    OnDeepLinkTappedListener onDeepLinkTappedListener;

    private CampaignsManager _campaignsManager = SessionM.getInstance().getCampaignsManager();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campaigns, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _campaignsManager.setListener(_campaignsListener);
        _messages = new ArrayList<>(_campaignsManager.getFeedMessages());

        _recyclerView = (RecyclerView) rootView.findViewById(R.id.message_feed_list);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _campaignsRecAdapter = new CampaignsRecAdapter(this, _messages);
        _recyclerView.setAdapter(_campaignsRecAdapter);
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

    public void onItemTapped(Message.MessageActionType actionType, String actionURL) {
        onDeepLinkTappedListener.onDeepLinkTapped(actionType, actionURL);
    }

    //On deep link listener to talk up to activity
    public interface OnDeepLinkTappedListener {
        void onDeepLinkTapped(Message.MessageActionType actionType, String actionURL);
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