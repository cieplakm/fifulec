package com.mmc.fifulec.di;

import dagger.Component;
import com.mmc.fifulec.NotifiService;
import com.mmc.fifulec.activity.ChallangeListActivity;
import com.mmc.fifulec.activity.LoginActivity;
import com.mmc.fifulec.activity.ResolveChallengeActivity;
import com.mmc.fifulec.activity.UserActivity;
import com.mmc.fifulec.activity.UserListActivity;

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

