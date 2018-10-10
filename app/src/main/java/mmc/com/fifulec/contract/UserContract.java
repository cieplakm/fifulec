package mmc.com.fifulec.contract;

public interface UserContract {
    interface View {
        void openUserListActivity();

        void setUserNickTitle(String nick);

        void openChallengesList();
    }
}
