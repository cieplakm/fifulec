package mmc.com.fifulec.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Challange {
    private String uuid;
    private String fromUserUuid;
    private String toUserUuid;
    private boolean isAccepted;
    private Score score;
}
