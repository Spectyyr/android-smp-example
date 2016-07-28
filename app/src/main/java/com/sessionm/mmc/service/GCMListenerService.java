/*
 * Copyright (c) 2015 SessionM. All rights reserved.
 */

package com.sessionm.mmc.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.sessionm.api.SessionM;
import com.sessionm.api.message.notification.data.NotificationMessage;
import com.sessionm.mmc.R;

public class GCMListenerService extends com.sessionm.api.message.notification.service.GCMListenerService {
    public static final int NOTIFICATION_ID = 1;

    /**
     * Uncomment this block to override sendNotification method for customized push.
     */
    /*@Override
    public void sendNotification(NotificationMessage notificationMessage) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        //By default and for now, open app
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (SessionM.getInstance().getMessageManager().shouldUseBundleExtras(this.getApplicationContext())) {
            Bundle messageBundle = new Bundle();
            messageBundle.putString(NotificationMessage.SESSIONM_MESSAGE_DATA_KEY, notificationMessage.getJsonString());
            LaunchIntent.putExtras(messageBundle);
        }
        PendingIntent openAppIntent = PendingIntent.getActivity(this, 0, LaunchIntent, 0);

        //By default, handle deep link.
        if (!SessionM.getInstance().getMessageManager().shouldUseBundleExtras(this.getApplicationContext())) {
            String url = notificationMessage.getActionURL();
            if (url != null && !url.isEmpty()) {
                openAppIntent = PendingIntent.getActivity(this, 0,
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)), 0);
            }
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(getNotificationSmallIcon())
                        .setLargeIcon(largeIcon)
                        .setContentTitle(notificationMessage.getTitle())
                        .setContentText(notificationMessage.getBody())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notificationMessage.getBody()))
                        .setContentIntent(openAppIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private int getNotificationSmallIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.mipmap.ic_push : R.mipmap.ic_launcher;
    }*/
}
