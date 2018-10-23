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
import io.reactivex.functions.BiFunction;
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
    }

    public void onChallengesClickedClicked() {
        view.openChallengesList();
    }

    public void onResume() {
        view.setUserNickTitle(appContext.getUser().getNick());

        final User user = appContext.getUser();


        cleanNotAcceptedRequests();

        final Observable<Challenge> challengeObservable = challengeService.challengesPerUser(user)
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

        Observable<Pair<Integer, Integer>> pairObservable = challengeObservable
                .flatMap(new Function<Challenge, ObservableSource<Scores>>() {
                    @Override
                    public ObservableSource<Scores> apply(Challenge challenge) throws Exception {
                        return Observable.fromIterable(challenge.getScores());
                    }
                })
                .map(new Function<Scores, Pair<Integer, Integer>>() {
                    @Override
                    public Pair<Integer, Integer> apply(Scores scores) throws Exception {
                        Score gainScore;
                        Score loseScore;
                        if (scores.getFrom().getUuid().equals(user.getUuid())) {
                            gainScore = scores.getFrom();
                            loseScore = scores.getTo();
                        } else {
                            gainScore = scores.getTo();
                            loseScore = scores.getFrom();
                        }
                        return new Pair<>(gainScore.getValue(), loseScore.getValue());
                    }
                });

        Observable<Integer> gainGools = pairObservable.map(new Function<Pair<Integer, Integer>, Integer>() {
            @Override
            public Integer apply(Pair<Integer, Integer> integerIntegerPair) throws Exception {
                return integerIntegerPair.first;
            }
        });
        Observable<Integer> loseGools = pairObservable.map(new Function<Pair<Integer, Integer>, Integer>() {
            @Override
            public Integer apply(Pair<Integer, Integer> integerIntegerPair) throws Exception {
                return integerIntegerPair.second;
            }
        });
        Observable<Integer> sumGain = MathObservable
                .sumInt(gainGools);
        Observable<Integer> loseSum = MathObservable
                .sumInt(loseGools);

        sumGain.zipWith(loseSum, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                return "+" + integer + "/-" + integer2;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                view.setGoolsBilance(s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        Observable<GroupedObservable<ChallengeScoreType, Scores>> groupedObservableObservable =
                challengeObservable
                        .flatMap(new Function<Challenge, ObservableSource<Scores>>() {
                            @Override
                            public ObservableSource<Scores> apply(Challenge challenge) throws Exception {
                                return Observable.fromIterable(challenge.getScores());
                            }
                        })
                .groupBy(new Function<Scores, ChallengeScoreType>() {
                    @Override
                    public ChallengeScoreType apply(Scores scores) throws Exception {
                        if (ChallengeHelper.isUserWin(user, scores)) {
                            return ChallengeScoreType.WIN;
                        } else if (scores.getFrom().getValue() == scores.getTo().getValue()) {
                            return ChallengeScoreType.DRAW;
                        } else {
                            return ChallengeScoreType.LOSE;
                        }
                    }
                });


        groupedObservableObservable
                .flatMap(new Function<GroupedObservable<ChallengeScoreType, Scores>, ObservableSource<Pair<ChallengeScoreType, Integer>>>() {
            @Override
            public ObservableSource<Pair<ChallengeScoreType, Integer>> apply(final GroupedObservable<ChallengeScoreType, Scores> challengeScoreTypeChallengeGroupedObservable) throws Exception {
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
    }

    private void cleanNotAcceptedRequests() {
        challengeService.cleanUnAcceptedRequest(appContext.getUser());
    }
}
