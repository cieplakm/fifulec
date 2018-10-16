package com.mmc.fifulec.repository;


import io.reactivex.Observable;

public interface SecurityRepository {

    Observable<String> uuidByNickObservable(String nick);

    void save(String nick, String uuid);
}
