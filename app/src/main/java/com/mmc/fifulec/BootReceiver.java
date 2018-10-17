package com.mmc.fifulec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TESTYY", "BOOT");
        Intent i = new Intent(context, RestartNotiServiceBroadcast.class);
        context.sendBroadcast(i);
    }
}
