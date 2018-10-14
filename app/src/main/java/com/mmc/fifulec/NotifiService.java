package com.mmc.fifulec;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.repository.ChallengeRepository;
import com.mmc.fifulec.utils.AppContext;

import javax.inject.Inject;

public class NotifiService extends Service {

    @Inject
    ChallengeRepository challengeRepository;

    @Inject
    AppContext appContext;
    private Disposable subscribe;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fifulec.component().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        subscribe = challengeRepository.listeningForChallengeLive(appContext.getUser().getUuid())
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Challenge>>() {
                    @Override
                    public void accept(List<Challenge> challenges) throws Exception {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            String chanel_id = "3000";
                            CharSequence name = "Channel Name";
                            String description = "Chanel Description";
                            int importance = NotificationManager.IMPORTANCE_LOW;
                            NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
                            mChannel.setDescription(description);
                            mChannel.enableLights(true);
                            mChannel.setLightColor(Color.BLUE);
                            manager.createNotificationChannel(mChannel);
                            builder = new NotificationCompat.Builder(getApplicationContext(), chanel_id);
                        } else {
                            builder = new NotificationCompat.Builder(getApplicationContext());
                        }

                        StringBuilder stringBuilder = new StringBuilder();
                        for (Challenge c : challenges){
                            stringBuilder.append(c.getFromUserNick());
                            stringBuilder.append(", ");
                        }

                        builder
                                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                .setAutoCancel(true)
                                .setContentTitle("Challenge!")
                                .setContentText("Challenge od " + stringBuilder.toString());

                        manager.notify(0, builder.build());
                    }
                });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscribe.dispose();
    }
}
