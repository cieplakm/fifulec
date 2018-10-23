package com.mmc.fifulec.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

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
        database.getReference()
                .child(CHALLANGES)
                .child(challenge.getUuid())
                .setValue(challenge);
    }

    @Override
    public void updateChallenge(Challenge challenge) {
        challengesReference.child(challenge.getUuid()).setValue(challenge);
    }

    @Override
    public void deleteChallenge(Challenge challenge){
        database.getReference()
                .child(CHALLANGES)
                .child(challenge.getUuid())
                .removeValue();
    }

    @Override
    public Observable<Challenge> getChallenge(final String uuid) {
        return Observable.create(new ObservableOnSubscribe<Challenge>() {
            @Override
            public void subscribe(final ObservableEmitter<Challenge> emitter) throws Exception {
                challengesReference.child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Challenge value = dataSnapshot.getValue(Challenge.class);
                        emitter.onNext(value);
                        emitter.onComplete();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        emitter.onError(new Exception());
                    }
                });
            }
        });
    }

    @Override
    public Observable<String> observeChallengeChanges(final String uuid){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                challengesReference.child(uuid)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                emitter.onNext(uuid);
                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                emitter.onError(new Exception());
                            }
                        });
            }
        });

    }
}
