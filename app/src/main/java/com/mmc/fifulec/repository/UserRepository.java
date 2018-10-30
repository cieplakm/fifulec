package com.mmc.fifulec.repository;

import com.mmc.fifulec.model.User;

import io.reactivex.Observable;


public interface UserRepository {
    void saveUser(User user);

    Observable<User> usersObservable();

    Observable<User> userByUuidObservable(String uuid);
}
