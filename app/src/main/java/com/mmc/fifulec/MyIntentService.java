package com.mmc.fifulec;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.repository.FirebaseChallengeRepository;
import com.mmc.fifulec.repository.FirebaseMappingRepository;
import com.mmc.fifulec.repository.FirebaseUserRepository;
import com.mmc.fifulec.service.ChallengeMappingService;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.service.FifulecNotification;
import com.mmc.fifulec.utils.Preferences;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class MyIntentService extends IntentService {

    private FirebaseUserRepository userRepository;
    private ChallengeService challengeService;
    private Preferences preferences;

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        userRepository = new FirebaseUserRepository(instance);
        preferences = new Preferences(this.getSharedPreferences("com.mmc.fifulec_preferences", MODE_PRIVATE));

        challengeService = new ChallengeService(new FirebaseChallengeRepository(instance), new ChallengeMappingService(new FirebaseMappingRepository(instance)));

        Observable<User> userObservable = Observable.just(false)
                .flatMap(new Function<Boolean, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(Boolean isUserInAppContext) {
                        if (isUserInAppContext) {
                            return null;
                        } else {
                            return userRepository.userByUuidObservable(preferences.getUuid());
                        }
                    }
                });

        Observable<String> stringObservable = userObservable
                .flatMap(new Function<User, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(User user) throws Exception {
                        return challengeService.observeChallengeChanges(user);
                    }
                });

        if (preferences.isNotificationActive())
            stringObservable.subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(String s) {
                    FifulecNotification fifulecNotification = new FifulecNotification(MyIntentService.this);
                    fifulecNotification.showChallengeChanged("KTOŚ");
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });

        return START_STICKY;
    }
}
