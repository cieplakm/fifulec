package com.mmc.fifulec.presenter;

import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.User;

public class ChallengeHelper {
    private ChallengeHelper() {
    }

    public static int getChallengeStatusWeight(Challenge o1) {
        switch (o1.getChallengeStatus()) {
            case NOT_CONFIRMED:
                return 5;
            case ACCEPTED:
                return 4;
            case NOT_ACCEPTED:
                return 3;
            case FINISHED:
                return 2;
            case REJECTED:
                return 1;
        }
        return 0;
    }

    public static boolean isUserWin(User user, Challenge challenge){
        return challenge.getScores().getFrom().getUuid().equals(user.getUuid()) && challenge.getScores().getFrom().getValue() > challenge.getScores().getTo().getValue() ||
                challenge.getScores().getTo().getUuid().equals(user.getUuid()) && challenge.getScores().getTo().getValue() > challenge.getScores().getFrom().getValue();
    }
}