package com.mmc.fifulec.presenter;

import com.mmc.fifulec.Security;
import com.mmc.fifulec.contract.LoginContract;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.service.UserService;
import com.mmc.fifulec.utils.AppContext;
import com.mmc.fifulec.utils.Preferences;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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

    public void onCreate(final LoginContract.View view) {
        this.view = view;

        String nick = preferences.getNick();
        if (nick != null) {
            userService
                    .userByNick(nick)
                    .subscribe(getLoginObserver());
        }
    }

    public void onLoginClicked(final String nick, final String pass) {
        final String properNick = nick.replace(" ", "");
        final String properPass = pass.replace(" ", "");

        userService
                .userByNick(properNick)
                .retry(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        userService.create(properNick, properPass);
                        return true;
                    }
                })
                .doOnNext(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        Security security = new Security();
                        String p = security.secure(properPass);
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
                preferences.putNick(user.getNick());
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
