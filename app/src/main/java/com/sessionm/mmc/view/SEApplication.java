/*
 * Copyright (c) 2015 SessionM. All rights reserved.
 */

package com.sessionm.mmc.view;

import android.app.Application;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMActivityLifecycleCallbacks;
import com.sessionm.mmc.R;
import com.sessionm.mmc.util.Utility;
import com.sessionm.mmc.view.custom.CustomLoaderView;

public class SEApplication extends Application{
    private static final String TAG = "AppController";
    private final SessionMActivityLifecycleCallbacks mCallbacks = new SessionMActivityLifecycleCallbacks();

    private static SEApplication instance;
    public static CustomLoaderView sampleCustomLoaderView;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Utility.initialize(this);
        //Creates SessionM activity lifecycle callbacks to handle activities lifecycle
        registerActivityLifecycleCallbacks(mCallbacks);
        final SessionM sessionM = SessionM.getInstance();
        sessionM.setApplicationContext(this);
        //sessionM.getExtension().setSessionAutoStartEnabled(false);
        //Sets server
        sessionM.setServerType(SessionM.SERVER_TYPE_CUSTOM, "PLEASE_SET_YOUR_CUSTOM_SERVER_HERE");
        //Sets the Google Cloud Messaging Sender ID, to register the app to be able to receive push notifications
        sessionM.setGCMSenderID(getString(R.string.gcm_sender_id));
        //Enables SessionM to receive push notifications, generates and sends a token to the server so the device can receive push notifications
        sessionM.setPushNotificationEnabled(true);
        //Enables SessionM to receive Feed Message Data (Message, FeedMessage)
        sessionM.setMessagesEnabled(true);
        //Enables SessionM to receive user activities data (receipts, orders, etc.)
        sessionM.setUserActivitiesEnabled(true);
    }

    public static SEApplication getInstance() {
        return instance;
    }
}
