package mmc.com.fifulec.service;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.Challange;
import mmc.com.fifulec.model.User;
import mmc.com.fifulec.repository.ChallangeRepository;
import mmc.com.fifulec.repository.UserRepository;

@AppScope
public class ChallangeService {

    private final ChallangeRepository repository;
    private final UserRepository userRepository;

    @Inject
    public ChallangeService(ChallangeRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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

    public Observable<Challange> observable(User user){
        Observable<Challange> objectObservable = repository.getChallangesFrom(user.getUuid(), null)
                .flatMap(new Function<Challange, ObservableSource<Challange>>() {
                    @Override
                    public ObservableSource<Challange> apply(final Challange challange) throws Exception {
                        return userRepository.getUserObs(challange.getFromUserUuid()).map(new Function<User, Challange>() {
                            @Override
                            public Challange apply(User user) throws Exception {
                                challange.setFromUserUuid(user.getNick());
                                return challange;
                            }
                        })
                                .flatMap(new Function<Challange, ObservableSource<Challange>>() {
                                    @Override
                                    public ObservableSource<Challange> apply(final Challange challange) throws Exception {
                                        return userRepository.getUserObs(challange.getToUserUuid()).map(new Function<User, Challange>() {
                                            @Override
                                            public Challange apply(User user) throws Exception {
                                                challange.setToUserUuid(user.getNick());
                                                return challange;
                                            }
                                        });

                                    }
                                });
                    }
                });

        return objectObservable;
    }


}
