/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_kotlin

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView

import com.squareup.picasso.Picasso

//Adapter class to draw the Promotions Message List and handle Feed Message events
class CampaignsRecAdapter(private val _fragment: CampaignsFragment, private val _messages: List<FeedMessage>) : RecyclerView.Adapter<CampaignsRecAdapter.CampaignsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.feed_item_campaign, parent, false)

        return CampaignsViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CampaignsViewHolder, position: Int) {
        val item = _messages[position]

        //Returns the message header
        holder.headerTextView.text = item.header

        //Returns the message sub header
        holder.subHeaderTextView.text = item.subHeader

        //Returns the message period
        holder.periodTextView.text = item.startTime + " - " + item.endTime

        //There is no need to draw the description if it was not set
        if (!TextUtils.isEmpty(item.details)) {
            //Returns the Message description, String
            holder.descriptionTextView.text = item.details
            holder.descriptionTextView.visibility = View.VISIBLE
        } else {
            holder.descriptionTextView.visibility = View.GONE
        }

        //TODO: set value, might be points
        val points = item.points
        if (points == 0)
            holder.valueTextView.visibility = View.GONE
        else
            holder.valueTextView.text = points.toString() + " pts"

        //Any customized value in data field
        /*JSONObject data = item.getData();
        if (data != null) {
            String value = data.optString("value");
            valueTextView.setText(value);
        }*/

        //There is no need to draw the image if there is not icon URL
        if (item.iconURL != null && item.iconURL != "null") {
            //Returns the Message image URL, String
            Picasso.with(_fragment.activity).load(item.iconURL).into(holder.iconImageView)
            holder.iconImageView.visibility = View.VISIBLE
        } else {
            holder.iconImageView.visibility = View.GONE
        }

        //There is no need to draw the image if there is not image URL
        val imageURL = item.imageURL
        if (imageURL != null && imageURL != "null") {
            if (imageURL.endsWith("mp4")) {
                val videoUri = Uri.parse(imageURL)
                holder.feedImageView.visibility = View.GONE
                holder.videoView.visibility = View.VISIBLE
                holder.videoView.setVideoURI(videoUri)
                holder.videoView.start()
                holder.videoView.setOnPreparedListener { }
                holder.videoView.setOnCompletionListener { }
            } else {
                //Returns the Message image URL, String
                Picasso.with(_fragment.activity).load(item.imageURL).into(holder.feedImageView)
                holder.feedImageView.visibility = View.VISIBLE
                holder.videoView.visibility = View.GONE
            }
        } else {
            holder.feedImageView.visibility = View.GONE
            holder.videoView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            item.notifyTapped()
            showDetails(item)
        }

        item.notifySeen()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return _messages.size
    }

    //TODO Needs to handle more events
    private fun showDetails(data: FeedMessage) {
        SessionM.getInstance().campaignsManager.executeMessageAction(data.messageID)
        val actionType = data.actionType
        if (actionType != FeedMessage.MessageActionType.FULL_SCREEN) {
            _fragment.onItemTapped(actionType, data.actionURL)
        }
    }

    class CampaignsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var iconImageView: ImageView = v.findViewById<ImageView>(R.id.promotion_icon_image) as ImageView
        internal var headerTextView: TextView = v.findViewById<TextView>(R.id.promotion_header_text) as TextView
        internal var subHeaderTextView: TextView = v.findViewById<TextView>(R.id.promotion_subheader_text) as TextView
        internal var periodTextView: TextView = v.findViewById<TextView>(R.id.promotion_period_text) as TextView
        internal var descriptionTextView: TextView = v.findViewById<TextView>(R.id.promotion_detail_text) as TextView
        internal var valueTextView: TextView = v.findViewById<TextView>(R.id.promotion_value_text) as TextView
        internal var feedImageView: ImageView = v.findViewById<ImageView>(R.id.promotion_main_image) as ImageView
        internal var videoView: VideoView = v.findViewById<VideoView>(R.id.promotion_main_video) as VideoView

    }
}