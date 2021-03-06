package com.mmc.fifulec.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mmc.fifulec.Fifulec;
import com.mmc.fifulec.contract.LoginContract;
import com.mmc.fifulec.presenter.LoginPresenter;
import com.mmc.fifulec.R;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume(this);
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
