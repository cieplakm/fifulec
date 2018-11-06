package com.mmc.fifulec.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.mmc.fifulec.R;
import com.mmc.fifulec.activity.ChallangeListActivity;
import com.mmc.fifulec.broadcastreciver.AcceptChallengeBR;
import com.mmc.fifulec.model.Challenge;

import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class FifulecNotification {

    private Context context;
    private final NotificationManager manager;

    public FifulecNotification(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showNewChallengeNotification(List<Challenge> challenges) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Challenge c : challenges) {
            stringBuilder.append(c.getFromUserNick());
            if (challenges.indexOf(c) != challenges.size() - 1) {
                stringBuilder.append(", ");
            }
        }

        Intent openIntent = new Intent(context, ChallangeListActivity.class);

        Intent accept = new Intent(context, AcceptChallengeBR.class);
        accept.putExtra("CHALLENGE", 1);
        accept.putExtra("CHALLENGE_ID", challenges.get(0).getUuid());

        Intent reject = new Intent(context, AcceptChallengeBR.class);
        reject.putExtra("CHALLENGE", 0);
        reject.putExtra("CHALLENGE_ID", challenges.get(0).getUuid());


        NotificationBuilder builder = new NotificationBuilder();
        builder.title("Nowy challenge!");
        builder.message(stringBuilder.toString());
        builder.addButtonAction(accept, R.drawable.circle_gray, "Akceptuj", 124);
        builder.addButtonAction(reject, R.drawable.circle_gray, "Odrzuć", 123);
        builder.clickAction(openIntent, 1623);

        manager.notify(22123, builder.build());
    }

    public void showChallengeChanged(String msg){
        Intent openIntent = new Intent(context, ChallangeListActivity.class);

        NotificationBuilder builder = new NotificationBuilder();
        builder.title("Nowa akcja!");
        builder.message(msg);

        builder.clickAction(openIntent, 1624);

        manager.notify(22124, builder.build());
    }

    public void cancel() {
        manager.cancel(22123);
    }

    private class NotificationBuilder {
        NotificationCompat.Builder builder;

        public Notification build() {
            return builder.build();
        }

        public NotificationBuilder() {
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

            builder
                    .setSmallIcon(R.drawable.circle_gray)
                    .setAutoCancel(true);
        }

        public NotificationBuilder title(String title) {
            builder.setContentTitle(title);
            return this;
        }

        public NotificationBuilder message(String msg) {
            builder.setContentText(msg);
            return this;
        }

        public NotificationBuilder addButtonAction(Intent intent, int drawable, String title, int requestCode) {
            PendingIntent pIntent = PendingIntent.getBroadcast(context, requestCode, intent, FLAG_UPDATE_CURRENT);
            builder.addAction(drawable, title, pIntent);
            return this;
        }

        public NotificationBuilder clickAction(Intent intent, int requestCode) {
            PendingIntent pAction = PendingIntent.getActivity(context, requestCode, intent, FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pAction);
            return this;
        }

    }
}
