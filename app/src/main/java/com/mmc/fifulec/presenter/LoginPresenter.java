package com.mmc.fifulec.presenter;

import javax.inject.Inject;

import com.mmc.fifulec.utils.AppContext;
import com.mmc.fifulec.utils.Preferences;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.contract.LoginContract;
import com.mmc.fifulec.service.CallBack;
import com.mmc.fifulec.service.UserService;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
        if (nick != null){

        }
    }

    public void onLoginClicked(final String nick, final String pass) {
        final String properNick = nick.replace(" ", "");
        final String properPass = pass.replace(" ", "");

        userService.userByNick(properNick).subscribe(getLoginObserver(properNick, properPass));

    }

    private Observer<? super User> getLoginObserver(final String properNick, final String properPass) {
        return  new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(User user) {
                view.showToast("Witaj! " + user.getNick());
                appContext.setUser(user);
                preferences.putNick(user.getNick());
                preferences.putPassword(user.getPassword());
                view.openUserActivity();
            }

            @Override
            public void onError(Throwable e) {
                userService.create(properNick, properPass);
                preferences.putNick(properNick);
                preferences.putPassword(properPass);
                view.showToast("Stworzono usera!");
                view.openUserActivity();
            }

            @Override
            public void onComplete() {

            }
        };
    }

}
