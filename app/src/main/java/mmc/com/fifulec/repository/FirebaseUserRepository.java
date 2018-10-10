package mmc.com.fifulec.repository;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.User;

@AppScope
public class FirebaseUserRepository implements UserRepository {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");

    @Inject
    public FirebaseUserRepository() {
    }

    @Override
    public void saveUser(User user) {
        reference.child(user.getUuid()).setValue(user);
    }

    @Override
    public User getUserByUuid(String uuid, ValueEventListener listener) {
        reference.child(uuid).addListenerForSingleValueEvent(listener);
        return null;
    }

    @Override
    public void getUsers(ValueEventListener valueEventListener) {
        reference.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public Observable<User> getUserObs(final String uuid) {
        Observable<User> userObservable = Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(final ObservableEmitter<User> emitter) throws Exception {
                reference.child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User value = dataSnapshot.getValue(User.class);
                        emitter.onNext(value);
                        emitter.onComplete();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        emitter.onError(new Exception());
                    }
                });
            }
        })
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());

        return userObservable;
    }

}
