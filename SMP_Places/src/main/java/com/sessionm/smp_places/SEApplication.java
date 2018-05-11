/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_places;

import android.app.Application;

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.core.api.StartupListener;

public class SEApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Callback is optional but highly recommended
        SessionM.start(this, new StartupListener() {
            @Override
            public void onStarted(SessionMError sessionMError) {
                //If sessionMError is not null, something is wrong(Networking, config, etc.)
            }
        });
    }
}
