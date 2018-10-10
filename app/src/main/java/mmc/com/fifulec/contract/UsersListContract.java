package mmc.com.fifulec.contract;

import java.util.List;

import mmc.com.fifulec.model.User;

public interface UsersListContract {
    interface View {
        void setUserList4Adapter(List<User> users);
    }
}
