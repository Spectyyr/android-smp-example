/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_campaigns;

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

import com.sessionm.campaign.api.CampaignsManager;
import com.sessionm.campaign.api.data.FeedMessage;
import com.sessionm.core.api.SessionMError;

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

    private CampaignsManager _campaignsManager = CampaignsManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campaigns, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _messages = new ArrayList<>(_campaignsManager.getFeedMessages());

        _recyclerView = rootView.findViewById(R.id.message_feed_list);
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
        _campaignsManager.fetchFeedMessages(new CampaignsManager.OnMessagesFetchedListener() {
            @Override
            public void onFetched(List<FeedMessage> list, SessionMError sessionMError) {
                _swipeRefreshLayout.setRefreshing(false);
                if (sessionMError != null)
                    Toast.makeText(getActivity(), "Failed: " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                else {
                    if (_messages == null) {
                        _messages = new ArrayList<>();
                    } else {
                        _messages.clear();
                    }
                    _messages.addAll(list);
                    _campaignsRecAdapter.notifyDataSetChanged();
                }
            }
        });
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