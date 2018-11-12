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

import com.mmc.fifulec.model.ChallengeMappingStatus;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

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
    public void update(String userId, ChallengeMapping mapping) {
        challengesReference
                .child(userId)
                .child(mapping.getUuid())
                .setValue(mapping);
    }

    @Override
    public Observable<ChallengeMapping> challengeMapiingObservable(String userId, String challengeId) {
        final Subject<ChallengeMapping> subject = PublishSubject.create();

        challengesReference.child(userId).orderByChild("challengeUuid").equalTo(challengeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ChallengeMapping value = child.getValue(ChallengeMapping.class);
                    value.setStatus(ChallengeMappingStatus.OLD);
                    subject.onNext(value);
                    subject.onComplete();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return subject;
    }

    @Override
    public void delete(String challengeUuid, String fromUserUuid, String toUserUuid){
        delete(challengeUuid, fromUserUuid);
        delete(challengeUuid, toUserUuid);
    }

    @Override
    public Observable<String> observeChanges(final String userUuid) {
        final Subject<String> subject = PublishSubject.create();
        challengesReference.child(userUuid).addChildEventListener(new MappingChangeListener(subject));
        return subject;
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

    class MappingChangeListener implements ChildEventListener {
        private Subject<String> subject;

        public MappingChangeListener(Subject<String> subject) {
            this.subject = subject;
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            ChallengeMapping value = dataSnapshot.getValue(ChallengeMapping.class);
            if (value != null && value.getStatus() != null && value.getStatus() != ChallengeMappingStatus.OLD){
                subject.onNext(value.getChallengeUuid());
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            ChallengeMapping value = dataSnapshot.getValue(ChallengeMapping.class);
            if (value != null && value.getStatus() != null && value.getStatus() != ChallengeMappingStatus.OLD){
                subject.onNext(value.getChallengeUuid());
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            subject.onError(new Exception());
        }
    }

}
