package com.mmc.fifulec.service;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeMapping;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.model.Score;
import com.mmc.fifulec.model.Scores;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.repository.ChallengeRepository;
import com.mmc.fifulec.repository.UserRepository;

@AppScope
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private ChallengeMappingService challengeMappingService;

    @Inject
    public ChallengeService(ChallengeRepository challengeRepository,
                            UserRepository userRepository,
                            ChallengeMappingService challengeMappingService) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.challengeMappingService = challengeMappingService;
    }


    public String createChallenge(User user, User toUser) {
        UUID uuid = UUID.randomUUID();
        Challenge challenge = Challenge.builder()
                .uuid(uuid.toString())
                .fromUserUuid(user.getUuid())
                .fromUserNick(user.getNick())
                .toUserNick(toUser.getNick())
                .toUserUuid(toUser.getUuid())
                .isAccepted(false)
                .challengeStatus(ChallengeStatus.NOT_ACCEPTED)
                .timestamp(System.currentTimeMillis())
                .build();
        challengeRepository.createChallenge(challenge);
        challengeMappingService.create(challenge);

        return uuid.toString();
    }

    public void acceptChallenge(Challenge challenge) {
        challenge.setAccepted(true);
        challenge.setChallengeStatus(ChallengeStatus.ACCEPTED);
        challengeRepository.updateChallenge(challenge);
    }

    public void rejectChallenge(Challenge challenge) {
        challenge.setChallengeStatus(ChallengeStatus.REJECTED);
        challengeRepository.updateChallenge(challenge);
    }

    public void resolveChallenge(Challenge challenge, int fromScore, int toScore) {
        Scores scores = Scores.builder()
                .from(new Score(challenge.getFromUserUuid(), fromScore))
                .to(new Score(challenge.getToUserUuid(), toScore))
                .build();

        challenge.setScores(scores);
        challenge.setChallengeStatus(ChallengeStatus.NOT_CONFIRMED);
        challengeRepository.updateChallenge(challenge);
    }

    public void confirmChallenge(Challenge challenge) {
        challenge.setChallengeStatus(ChallengeStatus.FINISHED);
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
}
