/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_kotlin

import android.app.Application
import com.sessionm.core.api.SessionM

class SEApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //Callback is optional but highly recommended
        SessionM.start(this) {
            //If sessionMError is not null, something is wrong(Networking, config, etc.)
        }
    }
}
