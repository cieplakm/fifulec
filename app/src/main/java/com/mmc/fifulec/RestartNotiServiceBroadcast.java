package com.mmc.fifulec;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class RestartNotiServiceBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Restart!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context, NotifiService.class);
        context.startService(i);

        Intent ii = new Intent(context, RestartNotiServiceBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +1000L, pendingIntent);

    }
}
