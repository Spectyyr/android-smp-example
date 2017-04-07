/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sessionm.api.SessionM;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        SessionM.getInstance().getMessageManager().setPushNotificationRegistrationID(refreshedToken);
    }

}
