package com.mmc.fifulec.contract;

public interface ResolveChallengeContract {
    interface View {

        void finish();

        void setTitles(String fromNick, String toNick);

        void showToast(String s);

        void setScores(String from, String to, String fromRew, String toRew);

        void setSecondMatchScoresVisibility(boolean isVisible);
    }

}
