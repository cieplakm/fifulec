package mmc.com.fifulec.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmc.com.fifulec.AppContext;
import mmc.com.fifulec.Fifulec;
import mmc.com.fifulec.R;
import mmc.com.fifulec.UserListPresenter;
import mmc.com.fifulec.UsersListContract;
import mmc.com.fifulec.model.User;
import mmc.com.fifulec.view.UserListAdapter;

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
