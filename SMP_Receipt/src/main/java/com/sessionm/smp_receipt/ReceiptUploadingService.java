package com.sessionm.smp_receipt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.receipt.ReceiptsListener;
import com.sessionm.api.receipt.data.Receipt;

import java.util.List;

public class ReceiptUploadingService extends Service {
    public static final int FOREGROUND_NOTIFICATION_ID = 11;
    public static final int RESULT_NOTIFICATION_ID = 22;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SessionM.getInstance().getReceiptsManager().setListener(_receiptListener);
        return super.onStartCommand(intent, flags, startId);
    }

    ReceiptsListener _receiptListener = new ReceiptsListener() {
        @Override
        public void onReceiptUploaded(Receipt receipt) {
            sendNotification("Success!", "Your receipt has been uploaded successfully!");
            stopSelf();
        }

        @Override
        public void onReceiptsFetched(List<Receipt> receiptList) {
        }

        @Override
        public void onProgress(Receipt receipt) {
            startForegroundNotification("Uploading receipt...", receipt.getImageURLs().size() + " images has been uploaded.");
        }

        @Override
        public void onFailure(SessionMError error) {
            sendNotification("Failed!", "Your receipt uploading is failed. Please try again later.");
            stopSelf();
        }
    };

    public void sendNotification(String title, String content) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(RESULT_NOTIFICATION_ID, buildLocalNotification(title, content));
    }

    public void startForegroundNotification(String title, String content) {
        startForeground(FOREGROUND_NOTIFICATION_ID, buildLocalNotification(title, content));
    }

    public Notification buildLocalNotification(String title, String content) {
        //By default and for now, open app
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent openAppIntent = PendingIntent.getActivity(this, 0, LaunchIntent, 0);

        PackageManager pm = getPackageManager();
        ApplicationInfo applicationInfo;
        int appIconResId = 0;
        try {
            applicationInfo = pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            appIconResId = applicationInfo.icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), appIconResId);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(appIconResId)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(openAppIntent).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
