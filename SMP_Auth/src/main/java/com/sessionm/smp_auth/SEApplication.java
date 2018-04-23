/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_auth;

import android.app.Application;

import com.sessionm.core.api.SessionM;

public class SEApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SessionM.start(this);
    }
}
