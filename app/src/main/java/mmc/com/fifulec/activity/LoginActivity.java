package mmc.com.fifulec.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmc.com.fifulec.Fifulec;
import mmc.com.fifulec.LoginContract;
import mmc.com.fifulec.LoginPresenter;
import mmc.com.fifulec.R;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    @BindView(R.id.et_nick)
    EditText etNick;

    @BindView(R.id.et_pass)
    EditText etPass;

    @Inject
    LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        Fifulec.component().inject(this);

        presenter.onCreate(this);

    }

    @OnClick(R.id.btn_login)
    public void onLoginClick(){
        presenter.onLoginClicked(etNick.getText().toString(), etPass.getText().toString());
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void openUserActivity() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
        finish();
    }
}
