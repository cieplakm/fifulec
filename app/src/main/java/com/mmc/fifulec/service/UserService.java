package com.mmc.fifulec.service;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
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

    public void getUUID(String nick, final CallBack<String> callBack) {
        securityRepository.getUuidByNick(nick, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uuid = dataSnapshot.getValue(String.class);
                callBack.response(uuid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Observable<User> getUsers() {
        return userRepository.usersObservable();
    }
}
