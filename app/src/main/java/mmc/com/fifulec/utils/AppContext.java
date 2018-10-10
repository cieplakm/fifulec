package mmc.com.fifulec.utils;

import javax.inject.Inject;

import lombok.Data;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.OnChallengeClickedListener;
import mmc.com.fifulec.model.OnUserClickedListener;
import mmc.com.fifulec.model.User;

@Data
@AppScope
public class AppContext {

    @Inject
    public AppContext() {
    }

    private User user;
    private OnUserClickedListener onUserClickedListener;
    private OnChallengeClickedListener onChallengeClickedListener;
}
