package mmc.com.fifulec.presenter;

import javax.inject.Inject;

import mmc.com.fifulec.contract.ResolveChallengeContract;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.Challenge;
import mmc.com.fifulec.service.ChallengeService;
import mmc.com.fifulec.utils.AppContext;

@AppScope
public class ResolveChallengePresenter {

    private ChallengeService challengeService;
    private AppContext appContext;

    @Inject
    public ResolveChallengePresenter(ChallengeService challengeService,
                                     AppContext appContext) {
        this.challengeService = challengeService;
        this.appContext = appContext;
    }

    private ResolveChallengeContract.View view;

    public void onCreate(ResolveChallengeContract.View view){
        this.view = view;
        Challenge challenge = appContext.getChallenge();
        view.setTitles(challenge.getFromUserNick(), challenge.getToUserNick());
    }

    public void onConfirmClicked(String from, String to) {
        int scoreFrom = Integer.parseInt(from);
        int scoreTo = Integer.parseInt(to);

        Challenge challenge = appContext.getChallenge();




        challengeService.resolveChallenge(challenge, scoreFrom, scoreTo);

        view.finish();
    }
}
