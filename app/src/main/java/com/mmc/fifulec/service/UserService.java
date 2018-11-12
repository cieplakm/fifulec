package com.mmc.fifulec.service;

import com.mmc.fifulec.utils.PasswordCrypter;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.repository.SecurityRepository;
import com.mmc.fifulec.repository.UserRepository;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

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

    private String create(String nick, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        PasswordCrypter passwordCrypter = new PasswordCrypter();
        User user = User.builder()
                .nick(nick)
                .password(passwordCrypter.crypt(password))
                .uuid(s)
                .build();
        securityRepository.save(nick, s);
        userRepository.saveUser(user);

        return s;
    }

    public Observable<User> userByNick(final String nick, final String pass) {
        return securityRepository.uuidByNickObservable(nick)
                .onErrorReturn(new Function<Throwable, String>() {
                    @Override
                    public String apply(Throwable throwable) throws Exception {
                        String s = create(nick, pass);
                        return s;
                    }
                })
                .flatMap(new Function<String, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(String uuid) throws Exception {
                        return userRepository.userByUuidObservable(uuid);
                    }
                });
    }

    public Observable<User> userByUuid(String uuid){
        return userRepository.userByUuidObservable(uuid);
    }

    public Observable<User> getUsers() {
        return userRepository.usersObservable();
    }
}
