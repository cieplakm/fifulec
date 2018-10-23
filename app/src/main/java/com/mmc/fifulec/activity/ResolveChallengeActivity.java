package com.mmc.fifulec.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import javax.inject.Inject;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mmc.fifulec.Fifulec;
import com.mmc.fifulec.R;
import com.mmc.fifulec.contract.ResolveChallengeContract;
import com.mmc.fifulec.presenter.ResolveChallengePresenter;

public class ResolveChallengeActivity extends AppCompatActivity implements ResolveChallengeContract.View{

    @BindView(R.id.et_score_from_user)
    EditText etScore4FromUser;
    @BindView(R.id.et_score_to_user)
    EditText etScore4ToUser;
    @BindView(R.id.et_score_from_user_rew)
    EditText etScore4FromUserRew;
    @BindView(R.id.et_score_to_user_rew)
    EditText etScore4ToUserRew;
    @BindView(R.id.second_match)
    LinearLayout llSecondMatch;

    @BindView(R.id.tv_from_user_nick)
    TextView tvFromUserNick;
    @BindView(R.id.tv_to_user_nick)
    TextView tvToUserNick;

    @Inject
    ResolveChallengePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolve_challenge);

        ButterKnife.bind(this);

        Fifulec.component().inject(this);

        presenter.onCreate(this);
    }

    @OnClick(R.id.btn_confirm)
    public void onConfirmClicked(){
        presenter.onConfirmClicked(etScore4FromUser.getText().toString(), etScore4ToUser.getText().toString(),
                etScore4FromUserRew.getText().toString(), etScore4ToUserRew.getText().toString());
    }

    @Override
    public void setTitles(String fromNick, String toNick){
        tvFromUserNick.setText(fromNick);
        tvToUserNick.setText(toNick);
    }

    @Override
    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setScores(String from, String to, String fromRew, String toRew) {
        etScore4FromUser.setText(from);
        etScore4ToUser.setText(to);
        etScore4FromUserRew.setText(fromRew);
        etScore4ToUserRew.setText(toRew);
    }

    @Override
    public void setSecondMatchScoresVisibility(boolean isVisible){
        llSecondMatch.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
