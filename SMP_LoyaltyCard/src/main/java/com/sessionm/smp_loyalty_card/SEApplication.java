/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_loyalty_card;

import android.app.Application;

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.core.api.StartupListener;
import com.sessionm.core.api.ext.SessionMExtension;
import com.sessionm.loyaltycard.api.LoyaltyCardsManager;

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

        //!!!!!!!Attention: this flag is for test only!!!!!!!!!!
        SessionMExtension.getInstance().setTestFlag(LoyaltyCardsManager.getInstance(), "_server_debug", true);
    }
}
