package mmc.com.fifulec.presenter;

import java.util.List;

import javax.inject.Inject;

import hu.akarnokd.rxjava2.math.MathObservable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import mmc.com.fifulec.model.Challenge;
import mmc.com.fifulec.model.ChallengeStatus;
import mmc.com.fifulec.model.Score;
import mmc.com.fifulec.model.Scores;
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
