package mmc.com.fifulec.repository;

import com.google.firebase.database.ValueEventListener;


import mmc.com.fifulec.model.User;

public interface UserRepository {
    void saveUser(User user);

    User getUserByUuid(String nick, ValueEventListener listener);

    void getUsers(ValueEventListener valueEventListener);
}
