package com.mmc.fifulec.broadcastreciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;
import com.mmc.fifulec.Notification;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.repository.FirebaseChallengeRepository;
import com.mmc.fifulec.repository.FirebaseMappingRepository;
import com.mmc.fifulec.repository.FirebaseSecurityRepository;
import com.mmc.fifulec.service.ChallengeMappingService;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.utils.Preferences;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

import static android.content.Context.MODE_PRIVATE;

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
            if (command==0){
                //reject
                Observable<Challenge> challenge = challengeRepository.getChallenge(id);
                challenge.subscribe(new Observer<Challenge>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Challenge challenge) {
                        challengeService.rejectChallenge(challenge);
                        new Notification(context).cancel();
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
                        challengeService.acceptChallenge(challenge);
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
    }
}
