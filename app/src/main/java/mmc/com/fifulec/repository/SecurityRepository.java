package mmc.com.fifulec.repository;

import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public interface SecurityRepository {

    String getUuidByNick(String nick, ValueEventListener listener);

    void save(String nick, String uuid);
}
