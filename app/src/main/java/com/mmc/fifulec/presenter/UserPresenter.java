package com.mmc.fifulec.presenter;

import java.util.List;

import javax.inject.Inject;

import hu.akarnokd.rxjava2.math.MathObservable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.model.Score;
import com.mmc.fifulec.model.Scores;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.utils.AppContext;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.contract.UserContract;

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

        final User user = appContext.getUser();

        Observable<Challenge> challengeObservable = challengeService.challengeFromUser(user)
                .mergeWith(challengeService.challengeToUser(user))
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        return challenge.getChallengeStatus() == ChallengeStatus.FINISHED;
                    }
                });

        challengeObservable
                .toList()
        .subscribe(new Consumer<List<Challenge>>() {
            @Override
            public void accept(List<Challenge> challenges) throws Exception {
                view.setAmountChallenges(challenges.size());
            }
        });

        Observable<Integer> streamOfGools = challengeObservable
                .map(new Function<Challenge, Integer>() {
                    @Override
                    public Integer apply(Challenge challenge) throws Exception {
                        Score score;
                        Scores scores = challenge.getScores();
                        if (scores.getFrom().getUuid().equals(user.getUuid())) {
                            score = scores.getFrom();
                        } else {
                            score = scores.getTo();
                        }
                        return score.getValue();
                    }
                });

        challengeObservable
                .filter(new Predicate<Challenge>() {
            @Override
            public boolean test(Challenge challenge) throws Exception {
                return challenge.getScores().getFrom().getUuid().equals(user.getUuid()) && challenge.getScores().getFrom().getValue() > challenge.getScores().getTo().getValue() ||
                challenge.getScores().getTo().getUuid().equals(user.getUuid()) && challenge.getScores().getTo().getValue() > challenge.getScores().getFrom().getValue();
            }
        })
                .count()
        .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                view.setWinsAmount(Long.toString(aLong));
            }
        });

        MathObservable
                .sumInt(streamOfGools)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        view.setGoolsAmount(Integer.toString(integer));
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
