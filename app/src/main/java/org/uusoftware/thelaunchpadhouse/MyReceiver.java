package org.uusoftware.thelaunchpadhouse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            scheduleAlarms(context);
        } else {
            Utils.generateNotification(context);
        }
    }

    public void scheduleAlarms(Context context) {
        int week = 1000 * 60 * 60 * 24 * 7;
        SharedPreferences prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        boolean alarmon = prefs.getBoolean("Alarm", true);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent myIntent = new Intent(context, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);

        Calendar calendar2 = Calendar.getInstance();

        if (alarmon) {
            if (calendar2.getTimeInMillis() < calendar.getTimeInMillis()) {
                alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), week, pendingIntent);
            } else {
                alarmManager.setInexactRepeating(AlarmManager.RTC, 1000 * 60 * 60 * 24 * 7 + calendar.getTimeInMillis(),
                        week, pendingIntent);
            }
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }
}