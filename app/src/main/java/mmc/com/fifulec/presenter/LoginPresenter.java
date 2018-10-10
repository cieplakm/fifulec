package mmc.com.fifulec.presenter;

import javax.inject.Inject;

import mmc.com.fifulec.utils.AppContext;
import mmc.com.fifulec.utils.Preferences;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.User;
import mmc.com.fifulec.contract.LoginContract;
import mmc.com.fifulec.service.CallBack;
import mmc.com.fifulec.service.UserService;

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
            userService.getUUID(nick, new CallBack<String>() {
                @Override
                public void response(String s) {
                    userService.getUser(s, new CallBack<User>() {
                        @Override
                        public void response(User user) {
                            appContext.setUser(user);
                            view.openUserActivity();
                        }

                        @Override
                        public void error() {

                        }
                    });
                }

                @Override
                public void error() {

                }
            });

        }
    }

    public void onLoginClicked(final String nick, final String pass) {
        userService.getUUID(nick, new CallBack<String>() {
            @Override
            public void response(final String uuid) {
                if (uuid != null){
                    userService.getUser(uuid, new CallBack<User>() {
                        @Override
                        public void response(User user) {
                            view.showToast("Witaj! " + user.getNick());
                            appContext.setUser(user);
                            preferences.putNick(user.getNick());
                            preferences.putPassword(user.getPassword());
                            view.openUserActivity();
                        }

                        @Override
                        public void error() {

                        }
                    });
                }else {
                    userService.create(nick, pass);
                    preferences.putNick(nick);
                    preferences.putPassword(pass);
                    view.showToast("Stworzono usera!");
                }
            }

            @Override
            public void error() {
                userService.create(nick, pass);
            }
        });


    }


}
