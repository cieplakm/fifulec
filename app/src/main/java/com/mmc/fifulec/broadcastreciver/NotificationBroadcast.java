package com.mmc.fifulec.broadcastreciver;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.mmc.fifulec.NotificationService;
import com.mmc.fifulec.service.FifulecNotification;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeMapping;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.repository.FirebaseChallengeRepository;
import com.mmc.fifulec.repository.FirebaseMappingRepository;
import com.mmc.fifulec.utils.Preferences;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class NotificationBroadcast extends BroadcastReceiver {

    private static final int JOB_ID = 19243245;

    @Override
    public void onReceive(final Context context, Intent intent) {


//        Preferences preferences = new Preferences(context.getSharedPreferences("com.mmc.fifulec_preferences", MODE_PRIVATE));
//
//        final String uuid = preferences.getUuid();
//
//        if (uuid != null) {
//            unAcceptedChallengesForUuid(uuid)
//                    .subscribe(new MaybeObserver<List<Challenge>>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onSuccess(List<Challenge> challenges) {
//                            FifulecNotification fifulecNotification = new FifulecNotification(context);
//                            fifulecNotification.showNewChallengeNotification(challenges);
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                        }
//
//                        @Override
//                        public void onComplete() {
//                        }
//                    });
//        }
    }

    public static boolean isJobServiceOn( Context context ) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService( Context.JOB_SCHEDULER_SERVICE ) ;

        boolean hasBeenScheduled = false ;

        for ( JobInfo jobInfo : scheduler.getAllPendingJobs() ) {
            if ( jobInfo.getId() == JOB_ID ) {
                hasBeenScheduled = true ;
                break ;
            }
        }

        return hasBeenScheduled ;
    }


    public Maybe<List<Challenge>> unAcceptedChallengesForUuid(final String uuid){
        FirebaseDatabase instance = FirebaseDatabase.getInstance();

        final FirebaseMappingRepository firebaseMappingRepository = new FirebaseMappingRepository(instance);
        final FirebaseChallengeRepository challengeRepository = new FirebaseChallengeRepository(instance);

        return firebaseMappingRepository.maping(uuid)
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
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        return challenge.getToUserUuid().equalsIgnoreCase(uuid);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .filter(new Predicate<List<Challenge>>() {
                    @Override
                    public boolean test(List<Challenge> challenges) throws Exception {
                        return challenges.size() > 0;
                    }
                });
    }


}
