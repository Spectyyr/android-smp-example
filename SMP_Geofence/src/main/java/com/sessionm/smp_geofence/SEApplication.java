/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_geofence;

import android.app.Application;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMActivityLifecycleCallbacks;
import com.sessionm.api.geofence.GeofenceManager;

public class SEApplication extends Application{
    private static final String TAG = "AppController";
    private final SessionMActivityLifecycleCallbacks _mCallbacks = new SessionMActivityLifecycleCallbacks();

    @Override
    public void onCreate() {
        super.onCreate();

        //Creates SessionM activity lifecycle callbacks to handle activities lifecycle
        registerActivityLifecycleCallbacks(_mCallbacks);
        final SessionM sessionM = SessionM.getInstance();
        sessionM.setApplicationContext(this);
        sessionM.setServerType(SessionM.SERVER_TYPE_CUSTOM, "https://api.tour-sessionm.com");

        //Enable push notification. Add this block to accept follow up push notification triggered by geofence events.

        //Sets the Google Cloud Messaging Sender ID, to register the app to be able to receive push notifications
        sessionM.getMessageManager().setGCMSenderID(getString(R.string.gcm_sender_id));
        //Enables SessionM to receive push notifications, generates and sends a token to the server so the device can receive push notifications
        sessionM.getMessageManager().setPushNotificationEnabled(true);
    }
}
