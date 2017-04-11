/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_auth;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMActivityLifecycleCallbacks;

public class SEApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        //Creates SessionM activity lifecycle callbacks to handle activities lifecycle
        registerActivityLifecycleCallbacks(new SessionMActivityLifecycleCallbacks());
        final SessionM sessionM = SessionM.getInstance();
        sessionM.setApplicationContext(this);
//        sessionM.setServerType(SessionM.SERVER_TYPE_CUSTOM, "https://api.tour-sessionm.com");
        sessionM.setAppKey("e2f1d6d709e44f5404a2091852617e60f473de85");
        sessionM.setServerType(SessionM.SERVER_TYPE_CUSTOM, "https://api-qa6.q-sessionm.com");
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
