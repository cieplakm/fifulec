package com.mmc.fifulec.repository;

import io.reactivex.Observable;
import com.mmc.fifulec.model.Challenge;

public interface ChallengeRepository {

    void createChallenge(Challenge challenge);

    void updateChallenge(Challenge challenge);

    Observable<Challenge> getChallengesFromUser(String uuid);

    Observable<Challenge> getChallengesToUser(String uuid);

    Observable<Challenge> listeningForChallengeLive(String uuid);
}
