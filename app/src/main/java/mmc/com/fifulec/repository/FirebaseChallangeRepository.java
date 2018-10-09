package mmc.com.fifulec.repository;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

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
    public void getChallangesFrom(String uuid, ValueEventListener listener) {
        database.getReference().child("challanges").orderByChild("fromUserUuid").equalTo(uuid);
    }

}
