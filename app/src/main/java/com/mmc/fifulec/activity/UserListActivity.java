package com.mmc.fifulec.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mmc.fifulec.utils.AppContext;
import com.mmc.fifulec.Fifulec;
import com.mmc.fifulec.R;
import com.mmc.fifulec.presenter.UserListPresenter;
import com.mmc.fifulec.contract.UsersListContract;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.view.UserListAdapter;

public class UserListActivity extends AppCompatActivity implements UsersListContract.View {

    @BindView(R.id.rv_user_list)
    RecyclerView recyclerView;

    @Inject
    UserListPresenter presenter;

    @Inject
    AppContext appContext;

    private UserListAdapter userListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        ButterKnife.bind(this);
        Fifulec.component().inject(this);

        presenter.onCreate(this);

        userListAdapter = new UserListAdapter(appContext.getOnUserClickedListener());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userListAdapter);
    }

    @Override
    public void setUserList4Adapter(List<User> users){
        userListAdapter.setUsers(users);
        userListAdapter.notifyDataSetChanged();
    }
}
