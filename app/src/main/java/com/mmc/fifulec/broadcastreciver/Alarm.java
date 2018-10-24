package com.mmc.fifulec.broadcastreciver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Alarm {
    private Context context;

    public Alarm(Context context) {
        this.context = context;
    }

    public void on(){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context, NotificationBroadcast.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000L, 60*1000L, pendingIntent);
    }

    public void off(){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context, NotificationBroadcast.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        am.cancel(pendingIntent);
    }
}
