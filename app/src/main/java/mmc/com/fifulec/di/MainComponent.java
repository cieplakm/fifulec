package mmc.com.fifulec.di;

import dagger.Component;
import mmc.com.fifulec.NotifiService;
import mmc.com.fifulec.activity.ChallangeListActivity;
import mmc.com.fifulec.activity.LoginActivity;
import mmc.com.fifulec.activity.ResolveChallengeActivity;
import mmc.com.fifulec.activity.UserActivity;
import mmc.com.fifulec.activity.UserListActivity;

@AppScope
@Component(modules =MainModule.class)
public interface MainComponent {

    void inject(LoginActivity loginActivity);
    void inject(UserActivity userActivity);
    void inject(UserListActivity userListActivity);
    void inject(ChallangeListActivity challangeListActivity);
    void inject(ResolveChallengeActivity resolveChallengeActivity);
    void inject(NotifiService notifiService);
}

