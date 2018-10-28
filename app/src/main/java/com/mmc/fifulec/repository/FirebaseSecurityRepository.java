package com.mmc.fifulec.repository;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmc.fifulec.di.AppScope;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
        return Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                        database.getReference().child(UUIDS).child(nick).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String uuid = dataSnapshot.getValue(String.class);
                                emitter.onNext(uuid);
                                emitter.onComplete();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                emitter.onError(new Exception());
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void save(String nick, String uuid) {
        database.getReference().child(UUIDS).child(nick).setValue(uuid);
    }
}
