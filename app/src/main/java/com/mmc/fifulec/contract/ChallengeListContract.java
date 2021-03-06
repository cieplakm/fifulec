package com.mmc.fifulec.contract;

import java.util.List;

import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.OnChallengeClickedListener;
import com.mmc.fifulec.model.OnChallengeConfirm;
import com.mmc.fifulec.model.User;

public interface ChallengeListContract {
    interface View {
        void setChallenges4Me(List<Challenge> challenges);

        void setUser(User user);

        void showUsersList();

        void setOnChallengeClickListener4Adapter(OnChallengeClickedListener onChallengeClickListener4Adapter);

        void showDaialogToAcceptChallenge(Challenge challenge);

        void setOnChallenge4MeClickListener(OnChallengeClickedListener onChallengeClickedListener);

        void openResolveActivity();

        void showConfirmDialog(OnChallengeConfirm onChallengeConfirm);

        void showToast(String message);

        void showDaialogToCancelChallenge(Challenge challenge);
    }
}
