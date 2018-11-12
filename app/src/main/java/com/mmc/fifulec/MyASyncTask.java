package com.mmc.fifulec;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.repository.FirebaseChallengeRepository;
import com.mmc.fifulec.repository.FirebaseMappingRepository;
import com.mmc.fifulec.repository.FirebaseUserRepository;
import com.mmc.fifulec.service.ChallengeMappingService;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.service.FifulecNotification;
import com.mmc.fifulec.utils.Preferences;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static android.content.Context.MODE_PRIVATE;

public class MyASyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = MyASyncTask.class.getSimpleName();

    Context context;


    private Disposable myASyncTask;

    private FirebaseUserRepository userRepository;
    private ChallengeService challengeService;
    private Preferences preferences;
    private User user;

    public MyASyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.i(TAG, "Bacground task is started " + this.toString());

        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        userRepository = new FirebaseUserRepository(instance);
        preferences = new Preferences(context.getSharedPreferences("com.mmc.fifulec_preferences", MODE_PRIVATE));

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


        Observable<Challenge> stringObservable = userObservable
                .flatMap(new Function<User, ObservableSource<String>>() {

                    @Override
                    public ObservableSource<String> apply(User user) throws Exception {
                        MyASyncTask.this.user = user;
                        return challengeService.observeChallengeChangesOrAdded(user);
                    }
                })
                .flatMap(new Function<String, ObservableSource<Challenge>>() {
                    @Override
                    public ObservableSource<Challenge> apply(String s) throws Exception {
                        return challengeService.challengePerUuid(s);
                    }
                })
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        if (challenge.getLastChangedById() == null) {
                            return false;
                        }
                        return !challenge.getLastChangedById().equals(user.getUuid());
                    }
                });


        stringObservable.subscribe(new Observer<Challenge>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Challenge challenge) {
                if (preferences.isNotificationActive()) {
                    FifulecNotification fifulecNotification = new FifulecNotification(context);
                    if (challenge.getChallengeStatus() == ChallengeStatus.NOT_ACCEPTED) {
                        fifulecNotification.showNewChallengeNotification(Collections.singletonList(challenge));
                    } else {
                        fifulecNotification.showChallengeChanged(getMessage(challenge));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


        return null;
    }

    private String getMessage(Challenge challenge) {
        String becouseOf;
        if (challenge.getFromUserUuid().equals(challenge.getLastChangedById())) {
            becouseOf = challenge.getFromUserNick();
        } else {
            becouseOf = challenge.getToUserNick();
        }

        String verb = "podjął akcję.";
        switch (challenge.getChallengeStatus()) {
            case NOT_ACCEPTED:
                verb = "wyzwał Cię!";
                break;
            case FINISHED:
                verb = "potwierdził wynik. Wyzwanie zostało zakończone.";
                break;
            case ACCEPTED:
                verb = "przyjął wyzwanie.";
                break;
            case NOT_CONFIRMED:
                verb = "wprowadził wynik i czeka na jego potwierdzenie.";
                break;
            case REJECTED:
                verb = "odrzucił wyzwanie.";
                break;
        }


        return becouseOf + " " + verb;
    }
}
