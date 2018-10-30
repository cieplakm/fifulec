package com.mmc.fifulec.broadcastreciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;
import com.mmc.fifulec.Notification;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeMapping;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.repository.FirebaseChallengeRepository;
import com.mmc.fifulec.repository.FirebaseMappingRepository;
import com.mmc.fifulec.repository.FirebaseSecurityRepository;
import com.mmc.fifulec.repository.FirebaseUserRepository;
import com.mmc.fifulec.repository.UserRepository;
import com.mmc.fifulec.service.ChallengeMappingService;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.utils.Preferences;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

public class RemoveUnAcceptedChallenges extends BroadcastReceiver {
    private static final long HOUR = 60*60 * 1000L;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Preferences preferences = new Preferences(context.getSharedPreferences("com.mmc.fifulec_preferences", MODE_PRIVATE));

        final String uuid = preferences.getUuid();


        FirebaseDatabase instance = FirebaseDatabase.getInstance();

        final FirebaseMappingRepository firebaseMappingRepository = new FirebaseMappingRepository(instance);
        final FirebaseChallengeRepository challengeRepository = new FirebaseChallengeRepository(instance);

        final ChallengeMappingService challengeMappingService = new ChallengeMappingService(firebaseMappingRepository);
        final ChallengeService challengeService = new ChallengeService(challengeRepository, challengeMappingService);


        challengeMappingService.mapping4User(uuid)
                .flatMap(new Function<ChallengeMapping, ObservableSource<Challenge>>() {
                    @Override
                    public ObservableSource<Challenge> apply(ChallengeMapping challengeMapping) throws Exception {
                        return challengeRepository.getChallenge(challengeMapping.getChallengeUuid());
                    }
                })
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        return challenge.getToUserUuid().equalsIgnoreCase(uuid);
                    }
                })
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        return challenge.getChallengeStatus() == ChallengeStatus.NOT_ACCEPTED
                                && challenge.getTimestamp() + HOUR < System.currentTimeMillis();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Challenge>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Challenge challenge) {
                        challengeService.delete(challenge);

                        new Notification(context).cancel();
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
