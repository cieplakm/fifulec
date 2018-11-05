package com.mmc.fifulec.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mmc.fifulec.R;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.OnChallengeClickedListener;
import com.mmc.fifulec.view.ChallengeAdapter;

public class ChallengeListFragment extends Fragment {

    @BindView(R.id.rv_challange_list)
    RecyclerView rvChallengesList;

    private ChallengeAdapter challengeAdapter = new ChallengeAdapter();
    private OnChallengeClickedListener onChallengeClickListener;

    public ChallengeListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_list, container, false);
        ButterKnife.bind(this, view);
        rvChallengesList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChallengesList.setAdapter(challengeAdapter);
        return view;
    }

    public void setChallenges4Adapter(List<Challenge> challenges) {
        challengeAdapter.setChallenges(challenges);
        challengeAdapter.notifyDataSetChanged();
    }

    public void setOnChallengeClickListener(OnChallengeClickedListener onChallengeClickListener){
        this.onChallengeClickListener = onChallengeClickListener;
        challengeAdapter.setOnChallengeClickedListener(onChallengeClickListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
