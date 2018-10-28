package com.mmc.fifulec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mmc.fifulec.broadcastreciver.NotificationBroadcast;
import com.mmc.fifulec.broadcastreciver.RemoveUnAcceptedChallenges;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, NotificationBroadcast.class);
        context.sendBroadcast(i);

        Intent ii = new Intent(context, RemoveUnAcceptedChallenges.class);
        context.sendBroadcast(ii);
    }
}
