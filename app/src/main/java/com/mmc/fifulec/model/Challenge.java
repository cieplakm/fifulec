package com.mmc.fifulec.model;

import com.google.firebase.database.Exclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {
    private String uuid;
    private String fromUserUuid;
    private String fromUserNick;
    private String toUserUuid;
    private String toUserNick;
    private boolean isAccepted;
    private ChallengeStatus challengeStatus;
    private int amountMatches;
    private Scores scores;
    private long timestamp;
}
