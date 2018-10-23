package com.mmc.fifulec.presenter;

import javax.inject.Inject;

import com.mmc.fifulec.contract.ResolveChallengeContract;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.Score;
import com.mmc.fifulec.model.Scores;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.utils.AppContext;

import java.util.List;

import dagger.internal.Preconditions;

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

        view.setSecondMatchScoresVisibility(challenge.isTwoLeggedTie());

        List<Scores> scores = challenge.getScores();

        if (scores != null){
            Scores s1 = scores.get(0);
            Scores s2 = null;

            if (scores.size() > 1){
                s2 = scores.get(1);
            }


            if (s1 != null && s2 !=null){
                view.setScores(
                        Integer.toString(s1.getFrom().getValue()),
                        Integer.toString(s1.getTo().getValue()),
                        Integer.toString(s2.getFrom().getValue()),
                        Integer.toString(s2.getTo().getValue())
                );
            }else if (s1 != null){
                view.setScores(
                        Integer.toString(s1.getFrom().getValue()),
                        Integer.toString(s1.getTo().getValue()),
                        "", "");
            }
        }
    }

    public void onConfirmClicked(String from, String to, String fromRew, String toRew) {
        Challenge challenge = appContext.getChallenge();

        try {
            if (from == null || from.isEmpty()) throw new Exception("Wynik musi być wpisany");
            if (to == null || to.isEmpty()) throw new Exception("Wynik musi być wpisany");
            if (challenge.isTwoLeggedTie()){
                if (fromRew == null || fromRew.isEmpty()) throw new Exception("Wynik musi być wpisany");
                if (toRew == null || toRew.isEmpty()) throw new Exception("Wynik musi być wpisany");
            }
        }catch (Exception e){
            view.showToast(e.getMessage());
            return;
        }

        int scoreFrom = Integer.parseInt(from);
        int scoreTo = Integer.parseInt(to);
        int scoreFromRew = 0;
        int scoreToRew = 0;
        if (challenge.isTwoLeggedTie()) {
            scoreFromRew = Integer.parseInt(fromRew);
            scoreToRew = Integer.parseInt(toRew);
        }

        challengeService.resolveChallenge(challenge, scoreFrom, scoreTo, scoreFromRew, scoreToRew);

        view.finish();
    }
}
