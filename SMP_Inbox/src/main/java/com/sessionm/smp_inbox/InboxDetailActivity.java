package com.sessionm.smp_inbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sessionm.inbox.api.InboxManager;
import com.sessionm.inbox.api.data.InboxMessage;

public class InboxDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_detail);

        int index = getIntent().getExtras().getInt("index");
        InboxMessage inboxMessage = InboxManager.getInstance().getInboxMessages().get(index);

        TextView subjectText = findViewById(R.id.inbox_detail_subject);
        TextView bodyText = findViewById(R.id.inbox_detail_body);

        subjectText.setText(inboxMessage.getSubject());
        bodyText.setText(inboxMessage.getBody());
    }
}
