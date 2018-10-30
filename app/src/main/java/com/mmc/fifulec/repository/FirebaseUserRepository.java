package com.mmc.fifulec.repository;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.User;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

@AppScope
public class FirebaseUserRepository implements UserRepository {
    public static final String USERS = "users";

    private FirebaseDatabase database;

    @Inject
    public FirebaseUserRepository(FirebaseDatabase database) {
        this.database = database;
    }

    @Override
    public void saveUser(User user) {
        database.getReference().child(USERS).child(user.getUuid()).setValue(user);
    }

    @Override
    public Observable<User> usersObservable() {
        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(final ObservableEmitter<User> emitter) throws Exception {
                database.getReference().child(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            User value = ds.getValue(User.class);
                            emitter.onNext(value);
                        }
                        emitter.onComplete();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        emitter.onError(new Exception());
                    }
                });
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<User> userByUuidObservable(final String uuid) {
        final Subject<User> subject = PublishSubject.create();

        database.getReference().child(USERS).child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                subject.onNext(value);
                subject.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                subject.onError(new Exception());
            }
        });

        return subject;
    }

}
