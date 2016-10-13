/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc_identity;

import android.app.Application;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMActivityLifecycleCallbacks;

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
    }
}
