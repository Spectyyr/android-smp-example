/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_geofence;

import android.app.Application;

import com.sessionm.api.SessionM;

public class SEApplication extends Application{
    private static final String TAG = "AppController";

    @Override
    public void onCreate() {
        super.onCreate();

        SessionM sessionM = SessionM.getInstance();
        sessionM.init(this);
        //Enables SessionM to receive push notifications, generates and sends a token to the server so the device can receive push notifications
        sessionM.getMessageManager().setPushNotificationEnabled(true);
    }
}
