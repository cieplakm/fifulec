package com.mmc.fifulec.utils;

import android.util.Pair;

import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeScoreType;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.model.Score;
import com.mmc.fifulec.model.Scores;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.presenter.ChallengeHelper;

import hu.akarnokd.rxjava2.math.MathObservable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;

public class StatsMaper {
    private Observable<Challenge> finishedChallenges;

    public StatsMaper(Observable<Challenge> challengeObservable) {
        finishedChallenges = challengeObservable
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        return challenge.getChallengeStatus() == ChallengeStatus.FINISHED;
                    }
                });
    }

    public Single<Long> amountChallenge() {
        return finishedChallenges
                .flatMap(new Function<Challenge, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Challenge challenge) throws Exception {
                        return Observable.fromIterable(challenge.getScores());
                    }
                }).count();
    }

    public Observable<String> goalBalance(final String userId) {

        Observable<Pair<Integer, Integer>> pairObservable = finishedChallenges
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
                        if (scores.getFrom().getUuid().equals(userId)) {
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

        return sumGain.zipWith(loseSum, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                return "+" + integer + "/-" + integer2;
            }
        });
    }

    public ObservableSource<Pair<ChallengeScoreType, Integer>> winDrawLose(final User user) {
        Observable<GroupedObservable<ChallengeScoreType, Scores>> groupedObservableObservable = finishedChallenges
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

        return groupedObservableObservable
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
                });
    }

}
