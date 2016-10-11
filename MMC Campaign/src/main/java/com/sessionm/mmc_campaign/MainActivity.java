package com.sessionm.mmc_campaign;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sessionm.api.message.data.Message;

public class MainActivity extends AppCompatActivity implements CampaignsFragment.OnDeepLinkTappedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onDeepLinkTapped(Message.MessageActionType actionType, String actionURL) {

    }
}
