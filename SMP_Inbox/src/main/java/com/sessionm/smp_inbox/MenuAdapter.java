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

import android.graphics.Color;
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
        holder.setMessage(inboxMessage);
        holder.setOnItemClickListener(mOnItemClickListener);
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subjectTextView;
        TextView timestampTextView;
        TextView bodyTextView;
        OnItemClickListener mOnItemClickListener;

        DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            subjectTextView = (TextView) itemView.findViewById(R.id.subject_textview);
            timestampTextView = (TextView) itemView.findViewById(R.id.timestamp_textview);
            bodyTextView = (TextView) itemView.findViewById(R.id.body_textview);
        }

        void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setMessage(InboxMessage message) {
            this.subjectTextView.setText(message.getSubject());
            this.timestampTextView.setText(message.getCreatedTime());
            this.bodyTextView.setText(message.getBody());
            if (message.getState().equals(InboxMessage.STATE_TYPES.NEW)) {
                subjectTextView.setTextColor(Color.BLACK);
                timestampTextView.setTextColor(Color.BLACK);
                bodyTextView.setTextColor(Color.BLACK);
            } else {
                subjectTextView.setTextColor(Color.GRAY);
                timestampTextView.setTextColor(Color.GRAY);
                bodyTextView.setTextColor(Color.GRAY);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
