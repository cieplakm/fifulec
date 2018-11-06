package com.mmc.fifulec.broadcastreciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.service.FifulecNotification;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.repository.FirebaseChallengeRepository;
import com.mmc.fifulec.repository.FirebaseMappingRepository;
import com.mmc.fifulec.service.ChallengeMappingService;
import com.mmc.fifulec.service.ChallengeService;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class AcceptChallengeBR extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        FirebaseDatabase instance = FirebaseDatabase.getInstance();

        final FirebaseMappingRepository firebaseMappingRepository = new FirebaseMappingRepository(instance);
        final FirebaseChallengeRepository challengeRepository = new FirebaseChallengeRepository(instance);

        final ChallengeMappingService challengeMappingService = new ChallengeMappingService(firebaseMappingRepository);
        final ChallengeService challengeService = new ChallengeService(challengeRepository, challengeMappingService);

        if (intent.hasExtra("CHALLENGE")){
            int command = intent.getExtras().getInt("CHALLENGE");
            String id = intent.getExtras().getString("CHALLENGE_ID");
            final String userId = intent.getExtras().getString("USER_ID");
            if (command==0){
                //reject
                Observable<Challenge> challenge = challengeRepository.getChallenge(id);
                challenge.subscribe(new Observer<Challenge>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Challenge challenge) {
                        User user = new User();
                        user.setUuid(userId);
                        challengeService.rejectChallenge(challenge, user);
                        new FifulecNotification(context).cancel();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }else {
                //accept
                Observable<Challenge> challenge = challengeRepository.getChallenge(id);
                challenge.subscribe(new Observer<Challenge>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Challenge challenge) {
                        User user = new User();
                        user.setUuid(userId);
                        challengeService.acceptChallenge(challenge, user);
                        new FifulecNotification(context).cancel();
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
    }
}
