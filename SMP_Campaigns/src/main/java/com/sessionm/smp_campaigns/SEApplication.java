/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_campaigns;

import android.app.Application;

public class SEApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SessionM.getInstance().startWithConfigFile(this);
    }
}
