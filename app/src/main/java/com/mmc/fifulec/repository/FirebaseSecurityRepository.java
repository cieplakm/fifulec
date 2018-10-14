package com.mmc.fifulec.repository;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import com.mmc.fifulec.di.AppScope;

@AppScope
public class FirebaseSecurityRepository implements SecurityRepository {

    FirebaseDatabase reference = FirebaseDatabase.getInstance();

    @Inject
    public FirebaseSecurityRepository() {
    }

    @Override
    public String getUuidByNick(String nick, ValueEventListener listener) {
        reference.getReference().child("uuids").child(nick).addListenerForSingleValueEvent(listener);

        return null;
    }

    @Override
    public void save(String nick, String uuid){
        reference.getReference().child("uuids").child(nick).setValue(uuid);
    }
}
