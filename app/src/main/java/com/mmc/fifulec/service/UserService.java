package com.mmc.fifulec.service;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.repository.SecurityRepository;
import com.mmc.fifulec.repository.UserRepository;

@AppScope
public class UserService {
    private final UserRepository userRepository;
    private SecurityRepository securityRepository;

    @Inject
    public UserService(UserRepository userRepository,
                       SecurityRepository securityRepository) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
    }

    public void create(String nick, String password){
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        User user = new User().builder()
                .nick(nick)
                .password(password)
                .uuid(s)
                .build();
        securityRepository.save(nick, s);
        userRepository.saveUser(user);
    }

    public void getUser(String uuid, final CallBack<User> callBack){
        userRepository.getUserByUuid(uuid, new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                callBack.response(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    public Observable<User> userByNick(String nick) {
        return securityRepository.getUuidByNick(nick)
                .flatMap(new Function<String, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(String uuid) throws Exception {
                        return userRepository.userObservable(uuid);
                    }
                });
    }

    public Observable<User> getUsers() {
        return userRepository.usersObservable();
    }
}
