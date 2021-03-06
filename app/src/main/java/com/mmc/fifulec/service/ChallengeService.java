package com.mmc.fifulec.service;

import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeMapping;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.model.Score;
import com.mmc.fifulec.model.Scores;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.presenter.ChallengeHelper;
import com.mmc.fifulec.repository.ChallengeRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

@AppScope
public class ChallengeService {

    private static final long HOUR = 1000L * 60 * 60;
    private final ChallengeRepository challengeRepository;
    private ChallengeMappingService challengeMappingService;

    @Inject
    public ChallengeService(ChallengeRepository challengeRepository,
                            ChallengeMappingService challengeMappingService) {
        this.challengeRepository = challengeRepository;
        this.challengeMappingService = challengeMappingService;
    }


    public String createChallenge(User user, User toUser, boolean isTwoLeggedTie) {
        UUID uuid = UUID.randomUUID();
        Challenge challenge = Challenge.builder()
                .uuid(uuid.toString())
                .fromUserUuid(user.getUuid())
                .fromUserNick(user.getNick())
                .toUserNick(toUser.getNick())
                .toUserUuid(toUser.getUuid())
                .challengeStatus(ChallengeStatus.NOT_ACCEPTED)
                .timestamp(System.currentTimeMillis())
                .twoLeggedTie(isTwoLeggedTie)
                .lastChangedById(user.getUuid())
                .build();
        challengeRepository.createChallenge(challenge);
        challengeMappingService.create(challenge);

        return uuid.toString();
    }

    public void delete(Challenge challenge) {
        challengeRepository.deleteChallenge(challenge);
        challengeMappingService.delete(challenge);
    }

    public void acceptChallenge(Challenge challenge, User user) {
        challenge.setChallengeStatus(ChallengeStatus.ACCEPTED);
        challenge.setLastChangedById(user.getUuid());
        challengeMappingService.markAsOldForChallenge(challenge.getFromUserUuid(), challenge.getToUserUuid(), challenge.getUuid());
        challengeRepository.updateChallenge(challenge);
    }

    public void rejectChallenge(Challenge challenge, User user) {
        challenge.setChallengeStatus(ChallengeStatus.REJECTED);
        challenge.setLastChangedById(user.getUuid());
        challengeRepository.updateChallenge(challenge);
    }

    public void resolveChallenge(Challenge challenge, User user, int fromScore, int toScore, int fromRew, int toRew) {
        Scores scores = Scores.builder()
                .from(new Score(challenge.getFromUserUuid(), fromScore))
                .to(new Score(challenge.getToUserUuid(), toScore))
                .build();

        List<Scores> scoresList;

        if (challenge.isTwoLeggedTie()) {
            Scores scoresRew = Scores.builder()
                    .from(new Score(challenge.getFromUserUuid(), fromRew))
                    .to(new Score(challenge.getToUserUuid(), toRew))
                    .build();

            scoresList = Arrays.asList(scores, scoresRew);
        } else {
            scoresList = Collections.singletonList(scores);
        }

        challenge.setScores(scoresList);
        challenge.setChallengeStatus(ChallengeStatus.NOT_CONFIRMED);
        challenge.setLastChangedById(user.getUuid());
        challengeRepository.updateChallenge(challenge);
    }

    public void confirmChallenge(Challenge challenge, User user) {
        challenge.setChallengeStatus(ChallengeStatus.FINISHED);
        challenge.setLastChangedById(user.getUuid());
        challengeRepository.updateChallenge(challenge);
    }

    public Observable<Challenge> challengesPerUser(User user) {
        return challengeMappingService.mapping4User(user.getUuid())
                .flatMap(new Function<ChallengeMapping, ObservableSource<Challenge>>() {
                    @Override
                    public ObservableSource<Challenge> apply(ChallengeMapping challengeMapping) throws Exception {
                        return challengeRepository.getChallenge(challengeMapping.getChallengeUuid());
                    }
                })
                .sorted(new Comparator<Challenge>() {
                    @Override
                    public int compare(Challenge o1, Challenge o2) {
                        int x = ChallengeHelper.getChallengeStatusWeight(o1);
                        int y = ChallengeHelper.getChallengeStatusWeight(o2);
                        if (x != y) {
                            return Integer.compare(x, y);
                        } else {
                            return Long.compare(o1.getTimestamp(), o2.getTimestamp());
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Challenge> getNotAcceptedChallenge(final User from, final User to) {
        return challengesPerUser(from)
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        return to.getUuid().equals(challenge.getFromUserUuid()) ||
                                to.getUuid().equals(challenge.getToUserUuid());
                    }
                })
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        return challenge.getChallengeStatus() == ChallengeStatus.NOT_ACCEPTED;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> observeChallengeChanges(User user){
        return challengesPerUser(user)
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        return challenge.getChallengeStatus() != ChallengeStatus.FINISHED
                                && challenge.getChallengeStatus() != ChallengeStatus.REJECTED;
                    }
                })
                .flatMap(new Function<Challenge, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Challenge challenge) throws Exception {
                        return challengeRepository.observeChallengeChanges(challenge.getUuid());
                    }
                });

    }

    public Observable<String> observeChallengeChangesOrAdded(User user) {
        Observable<String> challAdded = challengeMappingService.observeMappingChanges(user.getUuid());

        return challAdded
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        return challengeRepository.observeChallengeChanges(s);
                    }
                })
                .mergeWith(challAdded)
                .mergeWith(observeChallengeChanges(user))
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return s;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Challenge> challengePerUuid(String challengeId) {
        return challengeRepository.getChallenge(challengeId);
    }
}
