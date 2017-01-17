package com.sessionm.smp_geofence;

import android.content.Intent;

public class GeofenceService extends com.sessionm.api.geofence.service.GeofenceIntentService{

    public GeofenceService() {
        super();
    }

    @Override
    protected void onHandleIntent(Intent var1) {
        super.onHandleIntent(var1);
    }

    @Override
    public void sendNotification(String notificationDetails) {
        super.sendNotification(notificationDetails);
        RxBus.getInstance().setString(notificationDetails);
    }
}
