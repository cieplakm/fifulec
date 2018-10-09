package mmc.com.fifulec;

import java.util.List;

import javax.inject.Inject;

import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.User;
import mmc.com.fifulec.service.CallBack;
import mmc.com.fifulec.service.UserService;

@AppScope
public class UserListPresenter {

    private UsersListContract.View view;
    private UserService userService;

    @Inject
    public UserListPresenter(UserService userService) {
        this.userService = userService;
    }

    public void onCreate(final UsersListContract.View view) {
        this.view = view;

        userService.getUsers(new CallBack<List<User>>() {
            @Override
            public void response(List<User> users) {
                view.setUserList4Adapter(users);
            }

            @Override
            public void error() {

            }
        });
    }
}
