package mmc.com.fifulec.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmc.com.fifulec.Fifulec;
import mmc.com.fifulec.R;
import mmc.com.fifulec.contract.ResolveChallengeContract;
import mmc.com.fifulec.presenter.ResolveChallengePresenter;

public class ResolveChallengeActivity extends AppCompatActivity implements ResolveChallengeContract.View{

    @BindView(R.id.et_score_for_me)
    EditText etScore4Me;
    @BindView(R.id.et_score_for_they)
    EditText etScore4They;

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
        presenter.onConfrimClicked(etScore4Me.getText().toString(), etScore4They.getText().toString());
    }
}
