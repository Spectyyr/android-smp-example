/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_kotlin

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sessionm.campaign.api.CampaignsManager
import com.sessionm.campaign.api.data.FeedMessage
import java.util.*

/**
 * Fragment of SessionM List of Message
 */
class CampaignsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _swipeRefreshLayout: SwipeRefreshLayout? = null
    private var _campaignsRecAdapter: CampaignsRecAdapter? = null
    //private List of SessionM Message
    private var _messages: MutableList<FeedMessage>? = null
    private var _recyclerView: RecyclerView? = null

    private lateinit var onDeepLinkTappedListener: OnDeepLinkTappedListener

    private val _campaignsManager = CampaignsManager.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_campaigns, container, false)
        ViewCompat.setElevation(rootView, 50f)

        _swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout)
        _swipeRefreshLayout!!.setOnRefreshListener(this)

        _messages = ArrayList(_campaignsManager.feedMessages)

        _recyclerView = rootView.findViewById(R.id.message_feed_list)
        _recyclerView!!.setHasFixedSize(true)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        _recyclerView!!.layoutManager = llm
        _campaignsRecAdapter = CampaignsRecAdapter(this, _messages!!)
        _recyclerView!!.adapter = _campaignsRecAdapter
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _campaignsManager.fetchFeedMessages()
    }

    override fun onRefresh() {
        _campaignsManager.fetchFeedMessages { list, sessionMError ->
            _swipeRefreshLayout!!.isRefreshing = false
            if (sessionMError != null)
                Toast.makeText(activity, "Failed: " + sessionMError.message, Toast.LENGTH_SHORT).show()
            else {
                if (_messages == null) {
                    _messages = ArrayList()
                } else {
                    _messages!!.clear()
                }
                _messages!!.addAll(list)
                _campaignsRecAdapter!!.notifyDataSetChanged()
            }
        }
    }

    fun onItemTapped(actionType: FeedMessage.MessageActionType, actionURL: String) {
        onDeepLinkTappedListener.onDeepLinkTapped(actionType, actionURL)
    }

    //On deep link listener to talk up to activity
    interface OnDeepLinkTappedListener {
        fun onDeepLinkTapped(actionType: FeedMessage.MessageActionType, actionURL: String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            onDeepLinkTappedListener = context as OnDeepLinkTappedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString() + " must implement OnDeepLinkTappedListener")
        }

    }

    companion object {
        private val TAG = "FeedListActivity"
    }
}