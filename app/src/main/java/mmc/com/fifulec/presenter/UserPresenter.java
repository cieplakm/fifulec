package mmc.com.fifulec.presenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import mmc.com.fifulec.model.Challenge;
import mmc.com.fifulec.model.ChallengeStatus;
import mmc.com.fifulec.model.User;
import mmc.com.fifulec.service.ChallengeService;
import mmc.com.fifulec.utils.AppContext;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.contract.UserContract;

@AppScope
public class UserPresenter {

    private UserContract.View view;
    private AppContext appContext;
    private ChallengeService challengeService;

    @Inject
    public UserPresenter(AppContext appContext, ChallengeService challengeService){
        this.appContext = appContext;
        this.challengeService = challengeService;
    }

    public void onCreate(final UserContract.View view) {
        this.view = view;
        view.setUserNickTitle(appContext.getUser().getNick());

        User user = appContext.getUser();

        challengeService.challengeFromUser(user).mergeWith(challengeService.challengeToUser(user))
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        return challenge.getChallengeStatus() == ChallengeStatus.FINISHED;
                    }
                })
                .toList()
        .subscribe(new Consumer<List<Challenge>>() {
            @Override
            public void accept(List<Challenge> challenges) throws Exception {
                view.setAmountChallenges(challenges.size());
            }
        });
    }

    public void onUserListClicked() {
        view.openUserListActivity();
    }

    public void onChalangesClickedClicked() {
        view.openChallengesList();
    }
}
