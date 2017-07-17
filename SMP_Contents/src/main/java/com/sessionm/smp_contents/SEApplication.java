/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_contents;

import android.app.Application;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMActivityLifecycleCallbacks;

public class SEApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SessionM.getInstance().init(this);
    }
}
