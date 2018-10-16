package com.mmc.fifulec.presenter;

import android.util.Pair;

import com.mmc.fifulec.contract.UserContract;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeScoreType;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.model.Score;
import com.mmc.fifulec.model.Scores;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.utils.AppContext;

import java.util.List;

import javax.inject.Inject;

import hu.akarnokd.rxjava2.math.MathObservable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;

@AppScope
public class UserPresenter {

    private UserContract.View view;
    private AppContext appContext;
    private ChallengeService challengeService;

    @Inject
    public UserPresenter(AppContext appContext, ChallengeService challengeService) {
        this.appContext = appContext;
        this.challengeService = challengeService;
    }

    public void onCreate(final UserContract.View view) {
        this.view = view;
        view.setUserNickTitle(appContext.getUser().getNick());

        final User user = appContext.getUser();

        final Observable<Challenge> challengeObservable = challengeService.challengeFromUser(user)
                .mergeWith(challengeService.challengeToUser(user))
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        return challenge.getChallengeStatus() == ChallengeStatus.FINISHED;
                    }
                });

        challengeObservable
                .toList()
                .subscribe(new SingleObserver<List<Challenge>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Challenge> challenges) {
                        view.setAmountChallenges(challenges.size());
                    }

                    @Override
                    public void onError(Throwable e) {

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
                .groupBy(new Function<Challenge, ChallengeScoreType>() {
                    @Override
                    public ChallengeScoreType apply(Challenge challenge) throws Exception {
                        if (ChallengeHelper.isUserWin(user, challenge)) {
                            return ChallengeScoreType.WIN;
                        } else if (challenge.getScores().getFrom().getValue() == challenge.getScores().getTo().getValue()) {
                            return ChallengeScoreType.DRAW;
                        } else {
                            return ChallengeScoreType.LOSE;
                        }
                    }
                })
                .flatMap(new Function<GroupedObservable<ChallengeScoreType, Challenge>, ObservableSource<Pair<ChallengeScoreType, Integer>>>() {
                    @Override
                    public ObservableSource<Pair<ChallengeScoreType, Integer>> apply(final GroupedObservable<ChallengeScoreType, Challenge> challengeScoreTypeChallengeGroupedObservable) throws Exception {
                        return challengeScoreTypeChallengeGroupedObservable.count().toObservable()
                                .map(new Function<Long, Pair<ChallengeScoreType, Integer>>() {
                                    @Override
                                    public Pair<ChallengeScoreType, Integer> apply(Long aLong) throws Exception {
                                        return new Pair<>(challengeScoreTypeChallengeGroupedObservable.getKey(), aLong.intValue());
                                    }
                                });
                    }
                })
                .subscribe(new Observer<Pair<ChallengeScoreType, Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Pair<ChallengeScoreType, Integer> challengeScoreTypeIntegerPair) {
                        if (challengeScoreTypeIntegerPair.first == ChallengeScoreType.WIN) {
                            view.setWinsAmount(Long.toString(challengeScoreTypeIntegerPair.second));
                        } else if (challengeScoreTypeIntegerPair.first == ChallengeScoreType.DRAW) {
                            view.setDrawAmount(Long.toString(challengeScoreTypeIntegerPair.second));
                        } else {
                            view.setLoseAmount(Long.toString(challengeScoreTypeIntegerPair.second));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


        MathObservable
                .sumInt(streamOfGools)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        view.setGoolsAmount(Integer.toString(integer));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    public void onChallengesClickedClicked() {
        view.openChallengesList();
    }
}
