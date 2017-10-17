/*
 * Copyright (c) 2015 SessionM. All rights reserved.
 */

package com.sessionm.smp_push_notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.sessionm.api.message.notification.data.NotificationMessage;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class GCMListenerService extends com.sessionm.api.message.notification.service.GCMListenerService {
    public static final int NOTIFICATION_ID = 1;

    /**
     * Change this block to override sendNotification method for customized push.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void sendNotification(NotificationMessage pushNotificationData) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Notification.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel("test", "sefe", IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            mChannel.setDescription("TEST");
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);

            mBuilder = new Notification.Builder(this, "test")
                    .setSmallIcon(getNotificationSmallIcon())
                    .setLargeIcon(largeIcon)
                    .setAutoCancel(true)
                    .setContentTitle(pushNotificationData.getTitle())
                    .setContentText(pushNotificationData.getBody())
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(pushNotificationData.getBody()))
                    //Override pendingIntent to have customized behaviors
                    .setContentIntent(pendingIntent);
        } else {
            mBuilder =
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
        }
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private int getNotificationSmallIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.mipmap.ic_push : R.mipmap.ic_launcher;
    }
}
