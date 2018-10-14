package com.mmc.fifulec.repository;

import com.google.firebase.database.ValueEventListener;


import io.reactivex.Observable;
import com.mmc.fifulec.model.User;


public interface UserRepository {
    void saveUser(User user);

    User getUserByUuid(String nick, ValueEventListener listener);

    Observable<User> usersObservable();

    Observable<User> userObservable(String uuid);
}
