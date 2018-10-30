package com.mmc.fifulec.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmc.fifulec.model.ChallengeMapping;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class FirebaseMappingRepository implements ChallengeMappingRepository {

    private static final String CHALLENGE_MAPPING = "challengeMapping";
    private DatabaseReference challengesReference;

    @Inject
    public FirebaseMappingRepository(FirebaseDatabase database) {
        challengesReference = database.getReference().child(CHALLENGE_MAPPING);
    }

    @Override
    public void mapping(String userUuid, ChallengeMapping mapping) {
        challengesReference
                .child(userUuid)
                .child(mapping.getUuid())
                .setValue(mapping);
    }

    @Override
    public void delete(String challengeUuid, String fromUserUuid, String toUserUuid){
        delete(challengeUuid, fromUserUuid);
        delete(challengeUuid, toUserUuid);
    }

    @Override
    public Observable<String> observeChanges(final String userUuid) {
       return Observable.create(new ObservableOnSubscribe<String>() {
           @Override
           public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
               challengesReference.child(userUuid)
                       .addChildEventListener(new ChildEventListener() {
                           @Override
                           public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                               ChallengeMapping value = dataSnapshot.getValue(ChallengeMapping.class);
                               emitter.onNext(value.getChallengeUuid());
                           }

                           @Override
                           public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    private void delete(String challengeUuid, String userUuid){
        challengesReference
                .child(userUuid)
                .orderByChild("challengeUuid")
                .equalTo(challengeUuid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            dataSnapshot.child(key).getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public Observable<ChallengeMapping> maping(final String userUuid) {
        return Observable.create(new ObservableOnSubscribe<ChallengeMapping>() {
            @Override
            public void subscribe(final ObservableEmitter<ChallengeMapping> emitter) throws Exception {
                challengesReference
                        .child(userUuid)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ChallengeMapping mapping = snapshot.getValue(ChallengeMapping.class);
                                    emitter.onNext(mapping);
                                }
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
}
