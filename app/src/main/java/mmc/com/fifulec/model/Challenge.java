package mmc.com.fifulec.model;

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
    @Exclude
    private String fromUserNick;
    private String toUserUuid;
    @Exclude
    private String toUserNick;
    private boolean isAccepted;
    private ChallengeStatus challengeStatus;
    private Scores scores;
}
