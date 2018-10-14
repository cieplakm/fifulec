package com.mmc.fifulec.contract;

import java.util.List;

import com.mmc.fifulec.model.User;

public interface UsersListContract {
    interface View {
        void setUserList4Adapter(List<User> users);
    }
}
