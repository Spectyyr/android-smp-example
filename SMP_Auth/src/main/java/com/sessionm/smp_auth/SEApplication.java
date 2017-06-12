/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_auth;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMActivityLifecycleCallbacks;

public class SEApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SessionM.getInstance().init(this);
    }
}
