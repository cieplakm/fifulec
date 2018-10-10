package mmc.com.fifulec.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmc.com.fifulec.ChallengeListFragment;
import mmc.com.fifulec.contract.ChallengeListContract;
import mmc.com.fifulec.Fifulec;
import mmc.com.fifulec.R;
import mmc.com.fifulec.model.Challenge;
import mmc.com.fifulec.model.OnChallengeClickedListener;
import mmc.com.fifulec.model.OnChallengeConfirm;
import mmc.com.fifulec.presenter.ChallengeListPresenter;

public class ChallangeListActivity extends AppCompatActivity implements ChallengeListContract.View {

    @BindView(R.id.fl_challenges_4_me)
    FrameLayout flChallenges4Me;
    @BindView(R.id.fl_challenges_4_they)
    FrameLayout flChallenges4They;

    @Inject
    ChallengeListPresenter presenter;
    private ChallengeListFragment challenges4MeFragment;
    private ChallengeListFragment challenges4TheyFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challange_list);

        ButterKnife.bind(this);
        Fifulec.component().inject(this);

        challenges4MeFragment = new ChallengeListFragment();
        challenges4TheyFragment = new ChallengeListFragment();

        presenter.onCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @OnClick(R.id.btn_add_challange)
    public void onAddChallengeClicked(){
        presenter.onAddChallengeClicked();
    }

    @Override
    public void setChallenges4Me(List<Challenge> challenges) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_challenges_4_me, challenges4MeFragment)
                .commit();

        challenges4MeFragment.setChallenges4Adapter(challenges);

    }

    @Override
    public void setChallenges4They(List<Challenge> challenges) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_challenges_4_they, challenges4TheyFragment)
                .commit();

        challenges4TheyFragment.setChallenges4Adapter(challenges);
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
                        presenter.onChallengeAccepted(challenge);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
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
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy wynik zgadza się?").setPositiveButton("Tak", dialogClickListener)
                .setNegativeButton("Nie", dialogClickListener).show();
    }

}
