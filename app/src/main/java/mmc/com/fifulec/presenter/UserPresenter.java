package mmc.com.fifulec.presenter;

import javax.inject.Inject;

import mmc.com.fifulec.utils.AppContext;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.contract.UserContract;

@AppScope
public class UserPresenter {

    private UserContract.View view;
    private AppContext appContext;

    @Inject
    public UserPresenter(AppContext appContext){

        this.appContext = appContext;
    }

    public void onCreate(UserContract.View view) {
        this.view = view;
        view.setUserNickTitle(appContext.getUser().getNick());

    }

    public void onUserListClicked() {
        view.openUserListActivity();
    }

    public void onChalangesClickedClicked() {
        view.openChallengesList();
    }
}
