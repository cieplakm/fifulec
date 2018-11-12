package com.mmc.fifulec.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mmc.fifulec.Fifulec;
import com.mmc.fifulec.NotificationService;
import com.mmc.fifulec.R;
import com.mmc.fifulec.broadcastreciver.NotificationBroadcast;
import com.mmc.fifulec.contract.ChallengeListContract;
import com.mmc.fifulec.contract.UserContract;
import com.mmc.fifulec.fragment.ChallengeListFragment;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.OnChallengeClickedListener;
import com.mmc.fifulec.model.OnChallengeConfirm;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.presenter.ChallengeListPresenter;
import com.mmc.fifulec.presenter.UserPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserActivity extends AppCompatActivity implements UserContract.View, ChallengeListContract.View {

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

    private ChallengeListFragment challenges4MeFragment;

    @BindView(R.id.fl_challenges_4_me)
    FrameLayout flChallenges4Me;

    @Inject
    ChallengeListPresenter chPresenter;

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


        challenges4MeFragment = new ChallengeListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_challenges_4_me, challenges4MeFragment)
                .commit();

        ComponentName componentName = new ComponentName(this, NotificationService.class);
        JobInfo jobInfo = new JobInfo.Builder(123444, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(15*60*1000L)
                .build();

        JobScheduler jobScheduler = (JobScheduler)this.getSystemService(JOB_SCHEDULER_SERVICE);

        int resultCode = jobScheduler.schedule(jobInfo);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("SERVICY", "Job scheduled!");
        }

        chPresenter.onCreate(this);
        presenter.onCreate(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
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

//
//
//


    @OnClick(R.id.btn_add_challange)
    public void onAddChallengeClicked(){
        chPresenter.onAddChallengeClicked();
    }

    @Override
    public void setChallenges4Me(List<Challenge> challenges) {
        challenges4MeFragment.setChallenges4Adapter(challenges);
    }

    @Override
    public void setUser(User user){
        challenges4MeFragment.setUser(user);
    }

    @Override
    public void showUsersList(){
        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);
    }

    @Override
    public void setOnChallengeClickListener4Adapter(OnChallengeClickedListener onChallengeClickListener4Adapter){

    }

    @Override
    public void showDaialogToAcceptChallenge(final Challenge challenge) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        chPresenter.onAcceptedClicked(challenge);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        chPresenter.onRejectClicked(challenge);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy chcesz zaakceptować wyzwanie?").setPositiveButton("Tak", dialogClickListener)
                .setNegativeButton("Nie", dialogClickListener).show();
    }

    @Override
    public void setOnChallenge4MeClickListener(OnChallengeClickedListener onChallengeClickedListener) {
        challenges4MeFragment.setOnChallengeClickListener(onChallengeClickedListener);
    }

    @Override
    public void openResolveActivity() {
        Intent i = new Intent(this, ResolveChallengeActivity.class);
        startActivity(i);
    }

    @Override
    public void showConfirmDialog(final OnChallengeConfirm onChallengeConfirm) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        onChallengeConfirm.confirm();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        onChallengeConfirm.notConfirmed();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy wynik zgadza się?").setPositiveButton("Tak", dialogClickListener)
                .setNegativeButton("Nie", dialogClickListener).show();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDaialogToCancelChallenge(final Challenge challenge) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        chPresenter.onCancelClicked(challenge);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy chcesz anulować wyzwanie?").setPositiveButton("Tak", dialogClickListener)
                .setNegativeButton("Nie", dialogClickListener).show();
    }
}
