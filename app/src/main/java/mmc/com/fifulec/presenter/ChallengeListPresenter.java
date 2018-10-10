package mmc.com.fifulec.presenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import mmc.com.fifulec.model.Challenge;
import mmc.com.fifulec.model.OnChallengeClickedListener;
import mmc.com.fifulec.model.OnUserClickedListener;
import mmc.com.fifulec.model.User;
import mmc.com.fifulec.service.ChallengeService;
import mmc.com.fifulec.utils.AppContext;
import mmc.com.fifulec.contract.ChallengeListContract;
import mmc.com.fifulec.di.AppScope;


@AppScope
public class ChallengeListPresenter {

    private ChallengeListContract.View view;

    private final ChallengeService challengeService;
    private AppContext appContext;

    @Inject
    public ChallengeListPresenter(ChallengeService challengeService, AppContext appContext) {
        this.challengeService = challengeService;
        this.appContext = appContext;
    }

    public void onCreate(final ChallengeListContract.View view) {
        this.view = view;
        view.setOnChallengeClickListener4Adapter(new OnChallengeClickedListener() {
            @Override
            public void onChallengeSelect(Challenge challenge) {
                view.showDailogToAcceptChalange(challenge);
            }
        });
        updateChallengesList();
    }

    private void updateChallengesList() {
        challengeService.challengeFromUser(appContext.getUser())
                .toList()
                .subscribe(new Consumer<List<Challenge>>() {
                    @Override
                    public void accept(List<Challenge> challanges) throws Exception {
                        view.setChallenges4They(challanges);
                    }
                });

        challengeService.challengeToUser(appContext.getUser())
                .toList()
                .subscribe(new Consumer<List<Challenge>>() {
                    @Override
                    public void accept(List<Challenge> challanges) throws Exception {
                        view.setChallenges4Me(challanges);
                    }
                });
    }

    public void onAddChallangeClicked() {
        appContext.setOnUserClickedListener(new OnUserClickedListener() {
            @Override
            public void onUserSelect(User user) {
                challengeService.createChallenge(appContext.getUser(), user);
            }
        });

        view.showUsersList();
    }

    public void onChallengeAccepted(Challenge challenge) {
        challengeService.acceptChallenge(challenge);
    }

    public void onResume() {
        updateChallengesList();
    }
}
