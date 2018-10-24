package com.mmc.fifulec.broadcastreciver;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.mmc.fifulec.R;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeMapping;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.repository.FirebaseChallengeRepository;
import com.mmc.fifulec.repository.FirebaseMappingRepository;
import com.mmc.fifulec.repository.FirebaseSecurityRepository;
import com.mmc.fifulec.repository.FirebaseUserRepository;
import com.mmc.fifulec.repository.UserRepository;
import com.mmc.fifulec.utils.Preferences;

import java.util.List;

import io.reactivex.MaybeObserver;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

public class NotificationBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        final FirebaseChallengeRepository challengeRepository = new FirebaseChallengeRepository(instance);
        FirebaseSecurityRepository securityRepository = new FirebaseSecurityRepository();
        final FirebaseMappingRepository firebaseMappingRepository = new FirebaseMappingRepository(instance);
        final UserRepository userRepository = new FirebaseUserRepository();
        Preferences preferences = new Preferences(context.getSharedPreferences("com.mmc.fifulec_preferences", MODE_PRIVATE));

        String nick = preferences.getNick();

        if (nick != null){
            securityRepository.uuidByNickObservable(nick)
                    .flatMap(new Function<String, ObservableSource<ChallengeMapping>>() {
                        @Override
                        public ObservableSource<ChallengeMapping> apply(String uuid) throws Exception {
                            return firebaseMappingRepository.maping(uuid);
                        }
                    })
                    .flatMap(new Function<ChallengeMapping, ObservableSource<Challenge>>() {
                        @Override
                        public ObservableSource<Challenge> apply(ChallengeMapping challengeMapping) throws Exception {
                            return challengeRepository.getChallenge(challengeMapping.getChallengeUuid());
                        }
                    })
                    .filter(new Predicate<Challenge>() {
                        @Override
                        public boolean test(Challenge challenge) throws Exception {
                                 return challenge.getChallengeStatus() == ChallengeStatus.NOT_ACCEPTED;
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
                            showNotification(challenges, context);
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }
    }

    private void showNotification(List<String> challenges, Context context) {

        StringBuilder stringBuilder = new StringBuilder();
        for (String c : challenges){
            stringBuilder.append(c);
            if (challenges.indexOf(c) != challenges.size()-1){
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

        builder
                .setSmallIcon(R.drawable.circle_gray)
                .setAutoCancel(true)
                .setContentTitle("Nowy Challenge!")
                .setContentText("Od " + stringBuilder.toString());

        manager.notify(0, builder.build());
    }
}
