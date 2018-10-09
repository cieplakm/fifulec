package mmc.com.fifulec.service;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.User;
import mmc.com.fifulec.repository.SecurityRepository;
import mmc.com.fifulec.repository.UserRepository;

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

    public void getUsers(final CallBack<List<User>> callBack) {
        userRepository.getUsers(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> list = new ArrayList<User>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    list.add(child.getValue(User.class));
                }
                callBack.response(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}