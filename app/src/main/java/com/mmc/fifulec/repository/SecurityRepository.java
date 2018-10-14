package com.mmc.fifulec.repository;

import com.google.firebase.database.ValueEventListener;

public interface SecurityRepository {

    String getUuidByNick(String nick, ValueEventListener listener);

    void save(String nick, String uuid);
}
