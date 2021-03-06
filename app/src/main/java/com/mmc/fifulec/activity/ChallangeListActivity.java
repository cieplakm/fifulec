package com.mmc.fifulec.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mmc.fifulec.fragment.ChallengeListFragment;
import com.mmc.fifulec.contract.ChallengeListContract;
import com.mmc.fifulec.Fifulec;
import com.mmc.fifulec.R;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.OnChallengeClickedListener;
import com.mmc.fifulec.model.OnChallengeConfirm;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.presenter.ChallengeListPresenter;

public class ChallangeListActivity extends AppCompatActivity implements ChallengeListContract.View {

    private ChallengeListFragment challenges4MeFragment;

    @BindView(R.id.fl_challenges_4_me)
    FrameLayout flChallenges4Me;

    @Inject
    ChallengeListPresenter chPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challange_list);

        ButterKnife.bind(this);
        Fifulec.component().inject(this);

        challenges4MeFragment = new ChallengeListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_challenges_4_me, challenges4MeFragment)
                .commit();

        getSupportActionBar().setTitle("Aktywne wyzwania");

        chPresenter.onCreate(this);
    }

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
