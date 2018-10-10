package mmc.com.fifulec.presenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.User;
import mmc.com.fifulec.contract.UsersListContract;
import mmc.com.fifulec.service.CallBack;
import mmc.com.fifulec.service.UserService;
import mmc.com.fifulec.utils.AppContext;

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
