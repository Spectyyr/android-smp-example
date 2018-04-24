/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_kotlin

import android.app.Application
import com.sessionm.core.api.SessionM

class SEApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionM.start(this)
    }
}
