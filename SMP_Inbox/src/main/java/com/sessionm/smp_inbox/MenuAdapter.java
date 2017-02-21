/*
 * Copyright 2016 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sessionm.smp_inbox;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sessionm.api.inbox.data.InboxMessage;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

public class MenuAdapter extends SwipeMenuAdapter<MenuAdapter.DefaultViewHolder> {

    private List<InboxMessage> messages;

    private OnItemClickListener mOnItemClickListener;

    public MenuAdapter(List<InboxMessage> messages) {
        this.messages = messages;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
    }

    @Override
    public MenuAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new DefaultViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(MenuAdapter.DefaultViewHolder holder, int position) {
        InboxMessage inboxMessage = messages.get(position);
        String title = inboxMessage.getSubject();
        String timestamp = inboxMessage.getCreatedTime();
        String body = inboxMessage.getBody();
        if (inboxMessage.getState().equals(InboxMessage.STATE_TYPES.NEW)) {
            title += " NEW!!! ";
        }
        holder.setTitle(title);
        holder.setTimeStamp(timestamp);
        holder.setBody(body);
        holder.setOnItemClickListener(mOnItemClickListener);
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        TextView timestampTextView;
        TextView bodyTextView;
        OnItemClickListener mOnItemClickListener;

        DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            titleTextView = (TextView) itemView.findViewById(R.id.title_textview);
            timestampTextView = (TextView) itemView.findViewById(R.id.timestamp_textview);
            bodyTextView = (TextView) itemView.findViewById(R.id.body_textview);
        }

        void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setTitle(String title) {
            this.titleTextView.setText(title);
        }

        public void setTimeStamp(String timeStamp) {
            this.timestampTextView.setText(timeStamp);
        }

        public void setBody(String body) {
            this.bodyTextView.setText(body);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
