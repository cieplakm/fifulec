package mmc.com.fifulec.contract;

import java.util.List;

import mmc.com.fifulec.model.Challenge;
import mmc.com.fifulec.model.OnChallengeClickedListener;
import mmc.com.fifulec.model.OnChallengeConfirm;

public interface ChallengeListContract {
    interface View {
        void setChallenges4Me(List<Challenge> challenges);

        void setChallenges4They(List<Challenge> challenges);

        void showUsersList();

        void setOnChallengeClickListener4Adapter(OnChallengeClickedListener onChallengeClickListener4Adapter);

        void showDaialogToAcceptChallenge(Challenge challenge);

        void setOnChallenge4MeClickListener(OnChallengeClickedListener onChallengeClickedListener);

        void openResolveActivity();

        void showConfirmDialog(OnChallengeConfirm onChallengeConfirm);
    }
}
