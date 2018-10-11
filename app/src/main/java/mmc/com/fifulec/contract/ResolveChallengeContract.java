package mmc.com.fifulec.contract;

public interface ResolveChallengeContract {
    interface View {

        void finish();

        void setTitles(String fromNick, String toNick);

        void showToast(String s);
    }

}
