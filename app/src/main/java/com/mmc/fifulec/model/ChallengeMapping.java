package com.mmc.fifulec.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengeMapping {
    private String uuid;
    private String fromUuid;
    private String toUuid;
    private String challengeUuid;
}
