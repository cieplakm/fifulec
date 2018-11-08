package com.mmc.fifulec.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mmc.fifulec.Fifulec;
import com.mmc.fifulec.NotificationService;
import com.mmc.fifulec.R;
import com.mmc.fifulec.contract.UserContract;
import com.mmc.fifulec.presenter.UserPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserActivity extends AppCompatActivity implements UserContract.View {

    @BindView(R.id.tv_chalanges_amount)
    TextView tvUChallengesAmount;
    @BindView(R.id.tv_gools_balance)
    TextView tvGoolsBalance;
    @BindView(R.id.tv_wins_amount)
    TextView tvWinsAmount;
    @BindView(R.id.tv_draws_amount)
    TextView tvDrawsAmount;
    @BindView(R.id.tv_loses_amount)
    TextView tvLosesAmount;
    @BindView(R.id.sw_notification)
    Switch notiSwitch;

    @Inject
    UserPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);
        Fifulec.component().inject(this);

        notiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.onNotiSwitchChanged(buttonView.isChecked());
            }
        });
        presenter.onCreate(this);



        ComponentName componentName = new ComponentName(this, NotificationService.class);
        JobInfo jobInfo = new JobInfo.Builder(12, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setMinimumLatency(1000)
                .setOverrideDeadline(3 * 1000)
                .build();

        JobScheduler jobScheduler = (JobScheduler) this.getSystemService(JOB_SCHEDULER_SERVICE);

        int resultCode = jobScheduler.schedule(jobInfo);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("SERVICY", "Job scheduled!");
        } else {
            Log.d("SERVICY", "Job not scheduled");
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @OnClick(R.id.btn_challanges)
    public void onChalangesClicked() {
        presenter.onChallengesClickedClicked();
    }

    @Override
    public void setUserNickTitle(String nick) {
        getSupportActionBar().setTitle(nick);
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
    public void setGoolsBilance(String goolsAmount) {
        tvGoolsBalance.setText(goolsAmount);
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

    @Override
    public void setNotiSwitchActive(boolean notificationActive) {
        notiSwitch.setChecked(notificationActive);
    }
}
