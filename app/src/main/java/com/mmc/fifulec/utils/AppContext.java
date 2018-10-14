package com.mmc.fifulec.utils;

import javax.inject.Inject;

import lombok.Data;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.OnChallengeClickedListener;
import com.mmc.fifulec.model.OnUserClickedListener;
import com.mmc.fifulec.model.User;

@Data
@AppScope
public class AppContext {

    @Inject
    public AppContext() {
    }

    private User user;
    private Challenge challenge;
    private OnUserClickedListener onUserClickedListener;
    private OnChallengeClickedListener onChallengeClickedListener;
}
