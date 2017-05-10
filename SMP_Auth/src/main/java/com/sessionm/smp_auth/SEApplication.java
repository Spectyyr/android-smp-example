/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_auth;

import android.app.Application;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMActivityLifecycleCallbacks;

public class SEApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Creates SessionM activity lifecycle callbacks to handle activities lifecycle
        registerActivityLifecycleCallbacks(new SessionMActivityLifecycleCallbacks());
        final SessionM sessionM = SessionM.getInstance();
        sessionM.setApplicationContext(this);
        //        sessionM.setServerType(SessionM.SERVER_TYPE_CUSTOM, "https://api-miamiheat.stg-sessionm.com");
//        sessionM.setAppKey("848a5aa43f9bc95c7e97e3701264426ccb4c7c7b");
//        sessionM.setServerType(SessionM.SERVER_TYPE_CUSTOM, "https://api.tour-sessionm.com");
//        sessionM.setAppKey("c4d9427e894e3b0ddf67bd08280b11e051f66c82");

        sessionM.setServerType(SessionM.SERVER_TYPE_CUSTOM, "https://api-demo.stg-sessionm.com");
        sessionM.setAppKey("04e54a2689ec2d96e2cd5394f377671f80166165");


        //TODO: support native facebook SDK
//        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
