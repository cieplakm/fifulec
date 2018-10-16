package com.mmc.fifulec.repository;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Predicate;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeStatus;

@AppScope
public class FirebaseChallengeRepository implements ChallengeRepository {
    private static final String CHALLANGES = "challanges";

    private FirebaseDatabase database;
    private DatabaseReference challengesReference;

    @Inject
    public FirebaseChallengeRepository(FirebaseDatabase database) {
        this.database = database;
        challengesReference = database.getReference().child(CHALLANGES);
    }

    @Override
    public void createChallenge(Challenge challenge) {
        database.getReference().child(CHALLANGES).child(challenge.getUuid()).setValue(challenge);
    }

    @Override
    public void updateChallenge(Challenge challenge) {
        challenge.setAccepted(true);
        challengesReference.child(challenge.getUuid()).setValue(challenge);
    }

    @Override
    public Observable<Challenge> getChallengesFromUser(final String uuid) {
        Observable<Challenge> objectObservable = Observable.create(new ObservableOnSubscribe<Challenge>() {
            @Override
            public void subscribe(ObservableEmitter<Challenge> emitter) throws Exception {
                challengesReference.addListenerForSingleValueEvent(FirebaseChallengeRepository.this.subscribe(emitter));
            }
        });
        return objectObservable.filter(new Predicate<Challenge>() {
            @Override
            public boolean test(Challenge challenge) throws Exception {
                return challenge.getFromUserUuid().equals(uuid);
            }
        });
    }

    @Override
    public Observable<Challenge> getChallengesToUser(final String uuid) {
        Observable<Challenge> objectObservable = Observable.create(new ObservableOnSubscribe<Challenge>() {
            @Override
            public void subscribe(ObservableEmitter<Challenge> emitter) throws Exception {
                challengesReference.addListenerForSingleValueEvent(FirebaseChallengeRepository.this.subscribe(emitter));
            }
        });
        return objectObservable.filter(new Predicate<Challenge>() {
            @Override
            public boolean test(Challenge challenge) throws Exception {
                return challenge.getToUserUuid().equals(uuid);
            }
        });
    }

    @Override
    public Observable<Challenge> listeningForChallengeLive(final String uuid){
        return Observable.create(new ObservableOnSubscribe<Challenge>() {
            @Override
            public void subscribe(final ObservableEmitter<Challenge> emitter) throws Exception {
                challengesReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            Challenge value = ds.getValue(Challenge.class);
                            emitter.onNext(value);
                        }
                        emitter.onComplete();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        })
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        return challenge.getToUserUuid().equals(uuid)  && challenge.getChallengeStatus() == ChallengeStatus.NOT_ACCEPTED;
                    }
                });
    }

    private ValueEventListener subscribe(final ObservableEmitter<Challenge> emitter) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()){
                    emitter.onError(new Exception());
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Challenge challenge = ds.getValue(Challenge.class);
                    emitter.onNext(challenge);
                }
                emitter.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                emitter.onError(new Exception());
            }
        };
    }
}
