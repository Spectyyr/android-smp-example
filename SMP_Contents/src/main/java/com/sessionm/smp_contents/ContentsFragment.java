/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_contents;

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
import com.sessionm.api.content.ContentsListener;
import com.sessionm.api.content.ContentsManager;
import com.sessionm.api.content.data.Content;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment of SessionM List of Message
 */
public class ContentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "FeedListActivity";

    private SwipeRefreshLayout _swipeRefreshLayout;
    private ContentsRecAdapter _contentsRecAdapter;
    //private List of SessionM Message
    private List<Content> _contents;
    private RecyclerView _recyclerView;

    private ContentsManager _contentsManager = SessionM.getInstance().getContentsManager();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contents, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _contentsManager.setListener(_contentsListener);
        _contents = new ArrayList<>(_contentsManager.getContents());

        _recyclerView = (RecyclerView) rootView.findViewById(R.id.message_feed_list);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _contentsRecAdapter = new ContentsRecAdapter(this, _contents);
        _recyclerView.setAdapter(_contentsRecAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _contentsManager.fetchContents();
    }

    ContentsListener _contentsListener = new ContentsListener() {
        @Override
        public void onContentsFetched(List<Content> list, String s) {
            _swipeRefreshLayout.setRefreshing(false);
            if (_contents == null) {
                _contents = new ArrayList<>();
            } else {
                _contents.clear();
            }
            _contents.addAll(list);
            _contentsRecAdapter.notifyDataSetChanged();
        }

        @Override
        public void onSingleContentFetched(Content content) {

        }

        @Override
        public void onFailure(SessionMError sessionMError) {
            _swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Failed: " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        _contentsManager.setListener(_contentsListener);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        _contentsManager.fetchContents();
    }
}