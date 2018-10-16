package com.mmc.fifulec.repository;


import io.reactivex.Observable;

public interface SecurityRepository {

    Observable<String> getUuidByNick(String nick);

    void save(String nick, String uuid);
}
