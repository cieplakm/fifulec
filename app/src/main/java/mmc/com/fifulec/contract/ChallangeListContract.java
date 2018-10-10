package mmc.com.fifulec.contract;

import java.util.List;

import mmc.com.fifulec.model.Challange;

public interface ChallangeListContract {
    interface View {
        void setChalanges4Adapter(List<Challange> challanges);

        void showUsersList();
    }
}
