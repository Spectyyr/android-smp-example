/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_loyalty_card;

import android.app.Application;

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.ext.SessionMExtension;
import com.sessionm.loyaltycard.api.LoyaltyCardsManager;

public class SEApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SessionM.start(this);

        //!!!!!!!Attention: this flag is for test only!!!!!!!!!!
        SessionMExtension.getInstance().setTestFlag(LoyaltyCardsManager.getInstance(), "_server_debug", true);
    }
}
