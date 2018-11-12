package com.mmc.fifulec.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mmc.fifulec.R;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.model.OnChallengeClickedListener;
import com.mmc.fifulec.model.User;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {

    private List<Challenge> challenges;
    private OnChallengeClickedListener onChallengeClickedListener;
    private User me;

    public ChallengeAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.challange_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Challenge challenge = challenges.get(i);
        viewHolder.tvFromUser.setText(challenge.getFromUserNick());
        viewHolder.tvToUser.setText(challenge.getToUserNick());

        String stattitle = null;

        switch (challenge.getChallengeStatus()){
            case ACCEPTED:
                if (me.getUuid().equals(challenge.getFromUserUuid())){
                    stattitle="Wprowadź wynik";
                }else {
                    stattitle = "Oczekuje na wprowadzenie wyników";
                }
                break;
            case NOT_ACCEPTED:
                if (me.getUuid().equals(challenge.getFromUserUuid())){
                    stattitle="Czekaj na akceptację";
                }else {
                    stattitle = "Przyjmij lub odrzuć wyzwanie";
                }
                break;
            case FINISHED:
                stattitle="Zakończony";
                break;
            case REJECTED:
                stattitle="Odrzucony";
                break;
            case NOT_CONFIRMED:
                if (me.getUuid().equals(challenge.getFromUserUuid())){
                    stattitle="Czekaj na potwierdzenie wyniku";
                }else {
                    stattitle = "Potwierdź wynik";
                }
                break;


        }

        viewHolder.llRew.setVisibility(challenge.isTwoLeggedTie() ? View.VISIBLE : View.GONE);

        if (challenge.getScores() != null && (challenge.getChallengeStatus() == ChallengeStatus.NOT_CONFIRMED || challenge.getChallengeStatus() == ChallengeStatus.FINISHED)){
            viewHolder.tvFromScore.setText(Integer.toString(challenge.getScores().get(0).getFrom().getValue()));
            viewHolder.tvToScore.setText(Integer.toString(challenge.getScores().get(0).getTo().getValue()));

            if (challenge.isTwoLeggedTie()){
                viewHolder.tvFromScoreRew.setText(Integer.toString(challenge.getScores().get(1).getFrom().getValue()));
                viewHolder.tvToScoreRew.setText(Integer.toString(challenge.getScores().get(1).getTo().getValue()));
            }
        }else {
            viewHolder.tvFromScore.setText("-");
            viewHolder.tvToScore.setText("-");
            viewHolder.tvFromScoreRew.setText("-");
            viewHolder.tvToScoreRew.setText("-");
        }

        viewHolder.tvAccepted.setText(stattitle);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChallengeClickedListener != null){
                    onChallengeClickedListener.onChallengeSelect(challenge);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return challenges != null ? challenges.size() : 0;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public void setOnChallengeClickedListener(OnChallengeClickedListener challengeClickedListener) {
        this.onChallengeClickedListener = challengeClickedListener;
    }

    public void setMeUser(User me) {
        this.me = me;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_from_user_nick)
        TextView tvFromUser;
        @BindView(R.id.tv_to_user_nick)
        TextView tvToUser;
        @BindView(R.id.tv_accepted)
        TextView tvAccepted;
        @BindView(R.id.tv_from_score)
        TextView tvFromScore;
        @BindView(R.id.tv_to_score)
        TextView tvToScore;
        @BindView(R.id.tv_from_score_rew)
        TextView tvFromScoreRew;
        @BindView(R.id.tv_to_score_rew)
        TextView tvToScoreRew;
        @BindView(R.id.ll_rew)
        LinearLayout llRew;
         View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}