/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc_fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sessionm.api.message.notification.data.NotificationMessage;

public class GCMListenerService extends com.sessionm.api.message.notification.service.GCMListenerService {
    public static final int NOTIFICATION_ID = 1;

    /**
     * Change this block to override sendNotification method for customized push.
     */
    @Override
    public void sendNotification(NotificationMessage pushNotificationData) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(getNotificationSmallIcon())
                        .setLargeIcon(largeIcon)
                        .setAutoCancel(true)
                        .setContentTitle(pushNotificationData.getTitle())
                        .setContentText(pushNotificationData.getBody())
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(pushNotificationData.getBody()))
                        //Override pendingIntent to have customized behaviors
                        .setContentIntent(pendingIntent);
        //Multiple Action for future use
        //.addAction(0, pushNotificationData.getAction(), openAppIntent);
        //if (url != null && !url.isEmpty())
        //    mBuilder.addAction(0, pushNotificationData.getAction1(), openBrowserIntent);
        //mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private int getNotificationSmallIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }
}
