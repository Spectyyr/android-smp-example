package com.sessionm.smp_inbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.sessionm.api.SessionM;
import com.sessionm.api.inbox.data.InboxMessage;

public class InboxDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_detail);

        int index = getIntent().getExtras().getInt("index");
        InboxMessage inboxMessage = SessionM.getInstance().getInboxManager().getInboxMessages().get(index);

        TextView subjectText = (TextView) findViewById(R.id.inbox_detail_subject);
        TextView bodyText = (TextView) findViewById(R.id.inbox_detail_body);

        subjectText.setText(inboxMessage.getSubject());
        bodyText.setText(inboxMessage.getBody());
    }
}
