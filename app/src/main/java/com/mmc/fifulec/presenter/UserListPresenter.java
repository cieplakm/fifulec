package com.mmc.fifulec.presenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.contract.UsersListContract;
import com.mmc.fifulec.service.UserService;
import com.mmc.fifulec.utils.AppContext;

@AppScope
public class UserListPresenter {

    private UsersListContract.View view;
    private UserService userService;
    private AppContext appContext;

    @Inject
    public UserListPresenter(UserService userService, AppContext appContext) {
        this.userService = userService;
        this.appContext = appContext;
    }

    public void onCreate(final UsersListContract.View view) {
        this.view = view;

        userService.getUsers()
                .filter(new Predicate<User>() {
                    @Override
                    public boolean test(User user) throws Exception {
                        return !user.getUuid().equals( appContext.getUser().getUuid());
                    }
                })
                .toList()
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Exception {
                        view.setUserList4Adapter(users);
                    }
                });
    }
}
