package com.mmc.fifulec;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Toast.makeText(context, "Boot!", Toast.LENGTH_SHORT).show();

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(context, RestartNotiServiceBroadcast.class));

        }
    }
}
