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

    @Inject
    public ChallengeService(ChallengeRepository challengeRepository, UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }


    public void createChallenge(User user, User toUser) {
        UUID uuid = UUID.randomUUID();
        Challenge challenge = new Challenge().builder()
                .uuid(uuid.toString())
                .fromUserUuid(user.getUuid())
                .toUserUuid(toUser.getUuid())
                .isAccepted(false)
                .challengeStatus(ChallengeStatus.NOT_ACCEPTED)
                .build();
        challengeRepository.createChallenge(challenge);
    }

    public void acceptChallenge(Challenge challenge) {
        challenge.setAccepted(true);
        challenge.setChallengeStatus(ChallengeStatus.ACCEPTED);
        challengeRepository.updateChallenge(challenge);
    }

    public void finishChallenge(Challenge challenge) {
        challenge.setAccepted(true);
        challenge.setChallengeStatus(ChallengeStatus.FINISHED);
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

    public void confirmChallange(Challenge challenge) {
        challenge.setChallengeStatus(ChallengeStatus.FINISHED);
        challengeRepository.updateChallenge(challenge);
    }

    public Observable<Challenge> challengeFromUser(User user) {
        return challengeRepository.getChallengesFromUser(user.getUuid())
                .flatMap(new Function<Challenge, ObservableSource<Challenge>>() {
                    @Override
                    public ObservableSource<Challenge> apply(final Challenge challenge) throws Exception {
                        return userRepository.userObservable(challenge.getFromUserUuid())
                                .filter(new Predicate<User>() {
                                    @Override
                                    public boolean test(User user) throws Exception {
                                        return user.getUuid().equals(challenge.getFromUserUuid());
                                    }
                                })
                                .map(new Function<User, Challenge>() {
                                    @Override
                                    public Challenge apply(User user1) throws Exception {
                                        challenge.setFromUserNick(user1.getNick());
                                        return challenge;
                                    }
                                });
                    }
                })
                .flatMap(new Function<Challenge, ObservableSource<Challenge>>() {
                    @Override
                    public ObservableSource<Challenge> apply(final Challenge challange1) throws Exception {
                        return userRepository.userObservable(challange1.getToUserUuid())
                                .filter(new Predicate<User>() {
                                    @Override
                                    public boolean test(User user) throws Exception {
                                        return user.getUuid().equals(challange1.getToUserUuid());
                                    }
                                })
                                .map(new Function<User, Challenge>() {
                                    @Override
                                    public Challenge apply(User user1) throws Exception {
                                        challange1.setToUserNick(user1.getNick());
                                        return challange1;
                                    }
                                });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<Challenge> challengeToUser(User user) {
        return challengeRepository.getChallengesToUser(user.getUuid())
                .flatMap(new Function<Challenge, ObservableSource<Challenge>>() {
                    @Override
                    public ObservableSource<Challenge> apply(final Challenge challenge) throws Exception {
                        return userRepository.userObservable(challenge.getFromUserUuid())
                                .filter(new Predicate<User>() {
                                    @Override
                                    public boolean test(User user) throws Exception {
                                        return user.getUuid().equals(challenge.getFromUserUuid());
                                    }
                                })
                                .map(new Function<User, Challenge>() {
                                    @Override
                                    public Challenge apply(User user1) throws Exception {
                                        challenge.setFromUserNick(user1.getNick());
                                        return challenge;
                                    }
                                });
                    }
                })
                .flatMap(new Function<Challenge, ObservableSource<Challenge>>() {
                    @Override
                    public ObservableSource<Challenge> apply(final Challenge challange1) throws Exception {
                        return userRepository.userObservable(challange1.getToUserUuid())
                                .filter(new Predicate<User>() {
                                    @Override
                                    public boolean test(User user) throws Exception {
                                        return user.getUuid().equals(challange1.getToUserUuid());
                                    }
                                })
                                .map(new Function<User, Challenge>() {
                                    @Override
                                    public Challenge apply(User user1) throws Exception {
                                        challange1.setToUserNick(user1.getNick());
                                        return challange1;
                                    }
                                });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }


}
