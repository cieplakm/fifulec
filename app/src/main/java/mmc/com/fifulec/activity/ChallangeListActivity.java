package mmc.com.fifulec.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import mmc.com.fifulec.presenter.ChallengeListPresenter;
import mmc.com.fifulec.view.ChallengeAdapter;

public class ChallangeListActivity extends AppCompatActivity implements ChallengeListContract.View {

    @BindView(R.id.fl_challenges_4_me)
    FrameLayout flChallenges4Me;
    @BindView(R.id.fl_challenges_4_they)
    FrameLayout flChallenges4They;

    @Inject
    ChallengeListPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challange_list);

        ButterKnife.bind(this);
        Fifulec.component().inject(this);


        presenter.onCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @OnClick(R.id.btn_add_challange)
    public void onAddChallengeClicked(){
        presenter.onAddChallangeClicked();
    }

    @Override
    public void setChallenges4Me(List<Challenge> challenges) {
        ChallengeListFragment theyChallenges = new ChallengeListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_challenges_4_me, theyChallenges)
                .commit();

        theyChallenges.setChallenges4Adapter(challenges);

    }

    @Override
    public void setChallenges4They(List<Challenge> challenges) {
        ChallengeListFragment myChallanges = new ChallengeListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_challenges_4_they, myChallanges)
                .commit();

        myChallanges.setChallenges4Adapter(challenges);
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
    public void showDailogToAcceptChalange(final Challenge challenge) {
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

}
