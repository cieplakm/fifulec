package mmc.com.fifulec.contract;

public interface UserContract {
    interface View {
        void openUserListActivity();

        void setUserNickTitle(String nick);

        void openChallengesList();

        void setAmountChallenges(int size);

        void setGoolsAmount(String goolsAmount);

        void setWinsAmount(String wins);
    }
}
