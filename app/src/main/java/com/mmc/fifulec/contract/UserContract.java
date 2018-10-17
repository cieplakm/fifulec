package com.mmc.fifulec.contract;

public interface UserContract {
    interface View {

        void setUserNickTitle(String nick);

        void openChallengesList();

        void setAmountChallenges(int size);

        void setGoolsBilance(String goolsAmount);

        void setWinsAmount(String wins);

        void setDrawAmount(String draws);

        void setLoseAmount(String loses);
    }
}
