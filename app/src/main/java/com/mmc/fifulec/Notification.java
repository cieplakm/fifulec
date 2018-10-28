package com.mmc.fifulec;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.mmc.fifulec.broadcastreciver.AcceptChallengeBR;
import com.mmc.fifulec.model.Challenge;

import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class Notification {

    private Context context;

    public Notification(Context context) {
        this.context = context;
    }

    public void showNotification(List<Challenge> challenges) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Challenge c : challenges) {
            stringBuilder.append(c.getFromUserNick());
            if (challenges.indexOf(c) != challenges.size() - 1) {
                stringBuilder.append(", ");
            }
        }

        NotificationCompat.Builder builder;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chanel_id = "3000";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);

            manager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(context, chanel_id);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        Intent accept = new Intent(context, AcceptChallengeBR.class);
        accept.putExtra("CHALLENGE", 1);
        accept.putExtra("CHALLENGE_ID", challenges.get(0).getUuid());
        Intent reject = new Intent(context, AcceptChallengeBR.class);
        reject.putExtra("CHALLENGE", 0);
        reject.putExtra("CHALLENGE_ID", challenges.get(0).getUuid());

        PendingIntent paccept = PendingIntent.getBroadcast(context, 123, accept, FLAG_UPDATE_CURRENT);
        PendingIntent preject = PendingIntent.getBroadcast(context, 124, reject, FLAG_UPDATE_CURRENT);

        builder
                .setSmallIcon(R.drawable.circle_gray)
                .setAutoCancel(true)
                .setContentTitle("Nowy Challenge!")
                .setContentText("Od " + stringBuilder.toString())
                .addAction(R.drawable.common_google_signin_btn_icon_dark, "Zaakceptuj", paccept)
                .addAction(R.drawable.common_google_signin_btn_icon_dark, "Odrzuć", preject);

        manager.notify(22123, builder.build());

    }

    public void cancel(){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(22123);
    }
}
