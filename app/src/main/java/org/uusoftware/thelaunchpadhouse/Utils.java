package org.uusoftware.thelaunchpadhouse;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

public class Utils {

    public static NotificationManager mManager;

    public static void generateNotification(Context context) {

        Intent intent = new Intent(context, SplashActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_icon);

        Notification noti = new NotificationCompat.Builder(context).setContentTitle("The Looppad")
                .setContentText("It's Saturday. Let's chill out!").setSmallIcon(R.drawable.ic_icon)
                .setContentIntent(pIntent).setLargeIcon(bm).setDefaults(Notification.DEFAULT_ALL).build();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }
}