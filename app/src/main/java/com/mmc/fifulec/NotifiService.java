package com.mmc.fifulec;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.List;

import io.reactivex.MaybeObserver;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import com.google.firebase.database.FirebaseDatabase;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.repository.ChallengeRepository;
import com.mmc.fifulec.repository.FirebaseChallengeRepository;
import com.mmc.fifulec.repository.FirebaseSecurityRepository;
import com.mmc.fifulec.repository.FirebaseUserRepository;
import com.mmc.fifulec.repository.SecurityRepository;
import com.mmc.fifulec.repository.UserRepository;
import com.mmc.fifulec.utils.AppContext;
import com.mmc.fifulec.utils.Preferences;

import javax.inject.Inject;

public class NotifiService extends IntentService {

    ChallengeRepository challengeRepository;
    Preferences preferences;
    SecurityRepository securityRepository;


    public NotifiService(String name) {
        super(name);

    }

    public NotifiService() {
        super("namefgsdfgsdfg");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        challengeRepository = new FirebaseChallengeRepository(instance);
        securityRepository = new FirebaseSecurityRepository();
        final UserRepository userRepository = new FirebaseUserRepository();
        preferences = new Preferences(getApplication().getSharedPreferences("com.mmc.fifulec_preferences", MODE_PRIVATE));

        String nick = preferences.getNick();

        if (nick != null){
            securityRepository.uuidByNickObservable(nick)
                    .flatMap(new Function<String, ObservableSource<Challenge>>() {
                @Override
                public ObservableSource<Challenge> apply(String uuid) throws Exception {
                    return challengeRepository.listeningForChallengeLive(uuid);
                }
            })
                    .flatMap(new Function<Challenge, ObservableSource<User>>() {
                        @Override
                        public ObservableSource<User> apply(Challenge challenge) throws Exception {
                            return userRepository.userByUuidObservable(challenge.getFromUserUuid());
                        }
                    })
                    .map(new Function<User, String>() {
                        @Override
                        public String apply(User user) throws Exception {
                            return user.getNick();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toList()
                    .filter(new Predicate<List<String>>() {
                        @Override
                        public boolean test(List<String> challenges) throws Exception {
                            return challenges.size() > 0;
                        }
                    })
                    .subscribe(new MaybeObserver<List<String>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(List<String> challenges) {
                            showNotification(challenges);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
         return super.onStartCommand(intent, flags, startId);
    }



    private void showNotification(List<String> challenges) {

        StringBuilder stringBuilder = new StringBuilder();
        for (String c : challenges){
            stringBuilder.append(c);
            stringBuilder.append(", ");
        }

        NotificationCompat.Builder builder;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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
            builder = new NotificationCompat.Builder(getApplicationContext(), chanel_id);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        builder
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setAutoCancel(true)
                .setContentTitle("Challenge!")
                .setContentText("Challenge od " + stringBuilder.toString());

        manager.notify(0, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
