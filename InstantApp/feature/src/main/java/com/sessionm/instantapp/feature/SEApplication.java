/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.instantapp.feature;

import android.app.Application;

import com.sessionm.api.SessionM;

public class SEApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SessionM.getInstance().init(this);
    }
}