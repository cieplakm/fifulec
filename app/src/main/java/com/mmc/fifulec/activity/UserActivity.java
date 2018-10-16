package com.mmc.fifulec.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mmc.fifulec.Fifulec;
import com.mmc.fifulec.NotifiService;
import com.mmc.fifulec.R;
import com.mmc.fifulec.contract.UserContract;
import com.mmc.fifulec.presenter.UserPresenter;

public class UserActivity extends AppCompatActivity implements UserContract.View {

    @BindView(R.id.tv_user_nick)
    TextView tvUserNick;
    @BindView(R.id.tv_chalanges_amount)
    TextView tvUChallengesAmount;
    @BindView(R.id.tv_gools_amount)
    TextView tvGoolsAmount;
    @BindView(R.id.tv_wins_amount)
    TextView tvWinsAmount;
    @BindView(R.id.tv_draws_amount)
    TextView tvDrawsAmount;
    @BindView(R.id.tv_loses_amount)
    TextView tvLosesAmount;

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

    @OnClick(R.id.btn_challanges)
    public void onChalangesClicked(){
        presenter.onChallengesClickedClicked();
    }

    @Override
    public void setUserNickTitle(String nick){
        tvUserNick.setText(nick);
    }

    @Override
    public void openChallengesList() {
        Intent intent = new Intent(this, ChallangeListActivity.class);
        startActivity(intent);
    }

    @Override
    public void setAmountChallenges(int size) {
        tvUChallengesAmount.setText(Integer.toString(size));
    }

    @Override
    public void setGoolsAmount(String goolsAmount){
        tvGoolsAmount.setText(goolsAmount);
    }

    @Override
    public void setWinsAmount(String wins) {
        tvWinsAmount.setText(wins);
    }

    @Override
    public void setDrawAmount(String draws) {
        tvDrawsAmount.setText(draws);
    }

    @Override
    public void setLoseAmount(String loses) {
        tvLosesAmount.setText(loses);
    }
}
