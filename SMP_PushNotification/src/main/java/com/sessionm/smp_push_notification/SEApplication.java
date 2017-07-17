/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_push_notification;

import android.app.Application;

import com.sessionm.api.SessionM;

public class SEApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        SessionM.getInstance().init(this);
        //Enables SessionM to receive push notifications, generates and sends a token to the server so the device can receive push notifications
        SessionM.getInstance().getMessageManager().setPushNotificationEnabled(true);
    }
}
