package mmc.com.fifulec.model;

import lombok.Data;

@Data
class Score {
    private String firstUserUuid;
    private String secondUserUuid;
    private int score4FirstUser;
    private int score4SecondUser;
}
