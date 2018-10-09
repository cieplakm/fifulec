package mmc.com.fifulec.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmc.com.fifulec.Fifulec;
import mmc.com.fifulec.R;
import mmc.com.fifulec.UserContract;
import mmc.com.fifulec.UserPresenter;

public class UserActivity extends AppCompatActivity implements UserContract.View {

    @BindView(R.id.tv_user_nick)
    TextView tvUserNick;


    @Inject
    UserPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);
        Fifulec.component().inject(this);

        presenter.onCreate(this);
    }

    @OnClick(R.id.btn_users_list)
    public void onUsersListClicked(){
        presenter.onUserListClicked();
    }

    @Override
    public void openUserListActivity() {
        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);
    }

    @Override
    public void setUserNickTitle(String nick){
        tvUserNick.setText(nick);
    }
}