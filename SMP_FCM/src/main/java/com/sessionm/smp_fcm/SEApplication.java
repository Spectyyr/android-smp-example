/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_fcm;

import android.app.Application;

import com.sessionm.api.SessionM;
import com.sessionm.api.message.MessagesManager;

public class SEApplication extends Application{
@Override
    public void onCreate() {
        super.onCreate();
        //Creates SessionM activity lifecycle callbacks to handle activities lifecycle
        SessionM.getInstance().startWithConfigFile(this);
        //Enables SessionM to receive push notifications, generates and sends a token to the server so the device can receive push notifications
        if (!MessagesManager.getInstance().isPushNotificationEnabled())
            MessagesManager.getInstance().setPushNotificationEnabled(true);
    }

}
