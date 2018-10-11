package mmc.com.fifulec.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import javax.inject.Inject;

import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmc.com.fifulec.Fifulec;
import mmc.com.fifulec.R;
import mmc.com.fifulec.contract.ResolveChallengeContract;
import mmc.com.fifulec.presenter.ResolveChallengePresenter;

public class ResolveChallengeActivity extends AppCompatActivity implements ResolveChallengeContract.View{

    @BindView(R.id.et_score_from_user)
    EditText etScore4FromUser;
    @BindView(R.id.et_score_to_user)
    EditText etScore4ToUser;

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
        showToast(etScore4FromUser.getText().toString() + " : " + etScore4ToUser.getText().toString());
        presenter.onConfirmClicked(etScore4FromUser.getText().toString(), etScore4ToUser.getText().toString());
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
}
