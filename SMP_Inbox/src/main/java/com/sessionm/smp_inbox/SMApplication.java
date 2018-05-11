/*
 * Copyright 2018 Yan Zhenjie
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

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.core.api.StartupListener;

public class SMApplication extends Application {

    private static SMApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
        }
        //Callback is optional but highly recommended
        SessionM.start(this, new StartupListener() {
            @Override
            public void onStarted(SessionMError sessionMError) {
                //If sessionMError is not null, something is wrong(Networking, config, etc.)
            }
        });
    }

    public static SMApplication getInstance() {
        return instance;
    }
}
