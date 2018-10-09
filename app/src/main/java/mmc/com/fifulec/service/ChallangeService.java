package mmc.com.fifulec.service;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.Challange;
import mmc.com.fifulec.model.User;
import mmc.com.fifulec.repository.ChallangeRepository;

@AppScope
public class ChallangeService {

    private final ChallangeRepository repository;

    @Inject
    public ChallangeService(ChallangeRepository repository) {
        this.repository = repository;
    }


    public void createChallange(User user, User toUser) {
        UUID uuid = UUID.randomUUID();
        Challange challange = new Challange().builder()
                .uuid(uuid.toString())
                .fromUserUuid(user.getUuid())
                .toUserUuid(toUser.getUuid())
                .isAccepted(false)
                .build();
        repository.createChallange(challange);
    }

    public void acceptChallange(Challange challange) {
        challange.setAccepted(true);
        repository.updateChallange(challange);
    }

    public void resolveChallange(Challange challange) {
        //todo implement
    }

    public void getChallangesFromUser(User user, final CallBack<List<Challange>> callBack) {
        repository.getChallangesFrom(user.getUuid(),new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Challange> challanges = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Challange value = ds.getValue(Challange.class);
                    challanges.add(value);
                }
                callBack.response(challanges);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
