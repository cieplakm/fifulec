package mmc.com.fifulec.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.User;

@AppScope
public class FirebaseUserRepository implements UserRepository {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");

    @Inject
    public FirebaseUserRepository() {
    }

    @Override
    public void saveUser(User user) {
        reference.child(user.getUuid()).setValue(user);
    }

    @Override
    public User getUserByUuid(String uuid, ValueEventListener listener) {
        reference.child(uuid).addListenerForSingleValueEvent(listener);
        return null;
    }

    @Override
    public void getUsers(ValueEventListener valueEventListener) {
        reference.addListenerForSingleValueEvent(valueEventListener);
    }

}
