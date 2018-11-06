package com.mmc.fifulec.model;

import java.util.List;

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
    private ChallengeStatus challengeStatus;
    private int amountMatches;
    private List<Scores> scores;
    private long timestamp;
    private boolean twoLeggedTie;
    private String lastChangedById;
}
