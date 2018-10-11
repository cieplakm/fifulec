package mmc.com.fifulec;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import mmc.com.fifulec.activity.MainActivity;
import mmc.com.fifulec.model.Challenge;
import mmc.com.fifulec.repository.ChallengeRepository;
import mmc.com.fifulec.utils.AppContext;

import javax.inject.Inject;

public class NotifiService extends Service {

    @Inject
    ChallengeRepository challengeRepository;

    @Inject
    AppContext appContext;

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

        challengeRepository.listeningForChallengeLive(appContext.getUser().getUuid())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Challenge>() {
                    @Override
                    public void accept(Challenge challenge) throws Exception {
                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setAutoCancel(true)
                                        .setContentTitle("Challenge!")
                                        .setContentText("Challenge od " + challenge.getFromUserNick());

                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(0, builder.build());
                    }
                });

        return super.onStartCommand(intent, flags, startId);
    }


}
