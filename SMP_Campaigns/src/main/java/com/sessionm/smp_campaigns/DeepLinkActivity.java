package com.sessionm.smp_campaigns;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DeepLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);

        String url = getIntent().getStringExtra("url");

        TextView textView = (TextView) findViewById(R.id.deep_link_textview);
        textView.setText(url);
    }
}
