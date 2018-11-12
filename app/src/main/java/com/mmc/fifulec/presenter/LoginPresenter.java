package com.mmc.fifulec.presenter;

import com.mmc.fifulec.utils.PasswordCrypter;
import com.mmc.fifulec.contract.LoginContract;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.service.UserService;
import com.mmc.fifulec.utils.AppContext;
import com.mmc.fifulec.utils.Preferences;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

@AppScope
public class LoginPresenter {

    private UserService userService;
    private Preferences preferences;
    private AppContext appContext;
    private LoginContract.View view;

    @Inject
    public LoginPresenter(UserService userService,
                          Preferences preferences,
                          AppContext appContext) {
        this.userService = userService;
        this.preferences = preferences;
        this.appContext = appContext;
    }

    public void onResume(final LoginContract.View view) {
        this.view = view;

        String uuId = preferences.getUuid();
        if (uuId != null) {
            userService
                    .userByUuid(uuId)
                    .subscribe(getLoginObserver());
        }
    }

    public void onLoginClicked(final String nick, final String pass) {
        final String properNick = nick.replace(" ", "");
        final String properPass = pass.replace(" ", "");

        userService
                .userByNick(properNick)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(Observable<Throwable> throwableObservable) throws Exception {
                        userService.create(properNick, properPass);
                        return userService
                                .userByNick(properNick);
                    }
                })
                .doOnNext(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        PasswordCrypter passwordCrypter = new PasswordCrypter();
                        String p = passwordCrypter.crypt(properPass);
                        if (!user.getPassword().equalsIgnoreCase(p)){
                            throw new Exception("Błędne hasło");
                        }
                    }
                })

                .subscribe(getLoginObserver());
    }

    private Observer<? super User> getLoginObserver() {
        return new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(User user) {
                appContext.setUser(user);
                preferences.putUuid(user.getUuid());
                preferences.putPassword(user.getPassword());
                view.openUserActivity();
            }

            @Override
            public void onError(Throwable e) {
                view.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }

}
