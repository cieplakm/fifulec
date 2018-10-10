package mmc.com.fifulec.repository;

import io.reactivex.Observable;
import mmc.com.fifulec.model.Challenge;

public interface ChallengeRepository {

    void createChallenge(Challenge challenge);

    void updateChallenge(Challenge challenge);

    Observable<Challenge> getChallengesFromUser(String uuid);

    Observable<Challenge> getChallengesToUser(String uuid);
}
