package mmc.com.fifulec.presenter;

import javax.inject.Inject;

import mmc.com.fifulec.contract.ResolveChallengeContract;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.Challenge;
import mmc.com.fifulec.model.Score;
import mmc.com.fifulec.model.Scores;
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
    }

    public void onConfrimClicked(String me, String they) {
        int intMe = Integer.parseInt(me);
        int intThey = Integer.parseInt(they);

        Challenge challenge = appContext.getChallenge();

        Scores build;
        if (appContext.getUser().getUuid().equals(challenge.getFromUserUuid())){
            build = new Scores()
                    .builder()
                    .from(new Score(challenge.getFromUserUuid(), intMe))
                    .to(new Score(challenge.getToUserUuid(), intThey))
                    .build();
        }else {
            build = new Scores()
                    .builder()
                    .from(new Score(challenge.getToUserUuid(), intThey))
                    .to(new Score(challenge.getFromUserUuid(), intMe))
                    .build();
        }

        challenge.setScores(build);

        challengeService.resolveChallange(challenge);

        view.finish();
    }
}
