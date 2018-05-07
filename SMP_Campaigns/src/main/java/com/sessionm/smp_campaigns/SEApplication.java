/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_campaigns;

import android.app.Application;

import com.sessionm.core.api.SessionM;
import com.sessionm.identity.api.provider.SessionMOauthProvider;

public class SEApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SessionM.start(this, new SessionMOauthProvider());
    }
}
