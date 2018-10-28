package com.mmc.fifulec.broadcastreciver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Alarm {
    private Context context;

    public Alarm(Context context) {
        this.context = context;
    }

    public void on(Class<? extends BroadcastReceiver> br, long interval){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context, br);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000L, interval, pendingIntent);
    }

    public void off(Class<? extends BroadcastReceiver> br){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context, br);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        am.cancel(pendingIntent);
    }
}
