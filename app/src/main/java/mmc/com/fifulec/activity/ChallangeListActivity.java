package mmc.com.fifulec.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmc.com.fifulec.contract.ChallangeListContract;
import mmc.com.fifulec.Fifulec;
import mmc.com.fifulec.R;
import mmc.com.fifulec.model.Challange;
import mmc.com.fifulec.presenter.ChallangeListPresenter;
import mmc.com.fifulec.view.ChalangeAdapter;

public class ChallangeListActivity extends AppCompatActivity implements ChallangeListContract.View {

    @BindView(R.id.rv_challange_list)
    RecyclerView rvChallangesList;

    @Inject
    ChallangeListPresenter presenter;


    private ChalangeAdapter chalangeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challange_list);

        ButterKnife.bind(this);
        Fifulec.component().inject(this);

        presenter.onCreate(this);

        rvChallangesList.setLayoutManager(new LinearLayoutManager(this));
        chalangeAdapter = new ChalangeAdapter();
        rvChallangesList.setAdapter(chalangeAdapter);
    }

    @OnClick(R.id.btn_add_challange)
    public void onAddChallangeClicked(){
        presenter.onAddChallangeClicked();
    }

    @Override
    public void setChalanges4Adapter(List<Challange> challanges) {
        chalangeAdapter.setChallanges(challanges);
        chalangeAdapter.notifyDataSetChanged();
    }

    @Override
    public void showUsersList(){
        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);
    }

}
