package mmc.com.fifulec;

import java.util.List;

import mmc.com.fifulec.model.Challange;

public interface ChallangeListContract {
    interface View {
        void setChalanges4Adapter(List<Challange> challanges);

        void showUsersList();
    }
}
