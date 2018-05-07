/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_kotlin

import android.app.Application

import com.sessionm.core.api.SessionM
import com.sessionm.identity.api.provider.SessionMOauthProvider

class SEApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionM.start(this, SessionMOauthProvider())
    }
}
