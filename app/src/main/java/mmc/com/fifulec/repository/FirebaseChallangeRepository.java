package mmc.com.fifulec.repository;

import android.support.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.Challange;

@AppScope
public class FirebaseChallangeRepository implements ChallangeRepository {

    private FirebaseDatabase database;

    @Inject
    public FirebaseChallangeRepository(FirebaseDatabase database) {
        this.database = database;
    }

    @Override
    public void createChallange(Challange challange) {
        database.getReference().child("challanges").child(challange.getUuid()).setValue(challange);
    }

    @Override
    public void updateChallange(Challange challange) {
        challange.setAccepted(true);
        database.getReference().child("challanges").child(challange.getUuid()).setValue(challange);
    }

    @Override
    public Observable<Challange> getChallangesFrom(String uuid, ValueEventListener listener) {
        Observable<Challange> observable = Observable.create(new ObservableOnSubscribe<Challange>() {
            @Override
            public void subscribe(final ObservableEmitter<Challange> emitter) throws Exception {
                database.getReference().child("challanges").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            Challange challange = ds.getValue(Challange.class);
                            emitter.onNext(challange);
                        }
                        emitter.onComplete();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        emitter.onError(new Exception());
                    }
                });//todo not only for single user!!! but for all!

            }
        }).observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());

        return observable;

    }

}
