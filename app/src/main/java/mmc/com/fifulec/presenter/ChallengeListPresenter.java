package mmc.com.fifulec.presenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import mmc.com.fifulec.contract.ChallengeListContract;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.Challenge;
import mmc.com.fifulec.model.ChallengeStatus;
import mmc.com.fifulec.model.OnChallengeClickedListener;
import mmc.com.fifulec.model.OnChallengeConfirm;
import mmc.com.fifulec.model.OnUserClickedListener;
import mmc.com.fifulec.model.User;
import mmc.com.fifulec.service.ChallengeService;
import mmc.com.fifulec.utils.AppContext;

import static mmc.com.fifulec.model.ChallengeStatus.*;
import static mmc.com.fifulec.model.ChallengeStatus.ACCEPTED;


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
        updateChallengesList();

        view.setOnChallenge4MeClickListener(new OnChallengeClickedListener() {
            @Override
            public void onChallengeSelect(Challenge challenge) {
                switch (challenge.getChallengeStatus()) {
                    case ACCEPTED:
                        resolveChallenge(challenge);
                        break;
                    case NOT_ACCEPTED:
                        showQuestionAboutAcceptance(challenge);
                        break;
                    case FINISHED:
                        break;
                    case REJECTED:
                        break;
                    case NOT_CONFIRMED:
                        confirm(challenge);
                        break;
                }
            }
        });
    }

    private void confirm(final Challenge challenge) {
        view.showConfirmDialog(new OnChallengeConfirm() {
            @Override
            public void confirm() {
                challengeService.confirmChallange(challenge);
            }
        });
    }

    private void showQuestionAboutAcceptance(Challenge challenge) {
        view.showDaialogToAcceptChallenge(challenge);
    }

    private void resolveChallenge(Challenge challenge) {
        appContext.setChallenge(challenge);
        view.openResolveActivity();
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

    public void onAddChallengeClicked() {
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
