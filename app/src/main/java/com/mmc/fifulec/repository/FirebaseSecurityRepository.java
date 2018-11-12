package com.mmc.fifulec.repository;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmc.fifulec.di.AppScope;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

@AppScope
public class FirebaseSecurityRepository implements SecurityRepository {
    private static final String UUIDS = "uuids";

    private FirebaseDatabase database;

    @Inject
    public FirebaseSecurityRepository(FirebaseDatabase database) {
        this.database = database;
    }

    @Override
    public Observable<String> uuidByNickObservable(final String nick) {
        final Subject<String> subject = PublishSubject.create();

        database.getReference().child(UUIDS).child(nick).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uuid = dataSnapshot.getValue(String.class);
                if (uuid == null){
                    subject.onError(new RuntimeException("No user"));
                }else{
                    subject.onNext(uuid);
                    subject.onComplete();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                subject.onError(new Exception());
            }
        });

        return subject;
    }

    @Override
    public void save(String nick, String uuid) {
        database.getReference().child(UUIDS).child(nick).setValue(uuid);
    }
}
