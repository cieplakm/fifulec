package com.mmc.fifulec.presenter;

import javax.inject.Inject;

import com.mmc.fifulec.contract.ResolveChallengeContract;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.utils.AppContext;

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
