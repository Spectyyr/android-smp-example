/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_events;

import android.app.Application;

import com.sessionm.api.SessionM;

public class SEApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SessionM.getInstance().startWithConfigFile(this);
    }
}
