/*
 * Copyright 2016 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sessionm.smp_inbox;

import android.app.Application;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMActivityLifecycleCallbacks;

public class SMApplication extends Application {

    private static SMApplication instance;
    private final SessionMActivityLifecycleCallbacks _mCallbacks = new SessionMActivityLifecycleCallbacks();

    @Override
    public void onCreate() {
        super.onCreate();

        if (instance == null) {
            instance = this;
        }

        registerActivityLifecycleCallbacks(_mCallbacks);
        final SessionM sessionM = SessionM.getInstance();
        sessionM.setApplicationContext(this);
//        sessionM.setAppKey(getString(R.string.sessionm_app_key));
        sessionM.setServerType(SessionM.SERVER_TYPE_CUSTOM, "https://api.tour-sessionm.com");
    }

    public static SMApplication getInstance() {
        return instance;
    }
}
