package mmc.com.fifulec.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmc.com.fifulec.R;
import mmc.com.fifulec.model.Challenge;
import mmc.com.fifulec.model.ChallengeStatus;
import mmc.com.fifulec.model.OnChallengeClickedListener;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {

    private List<Challenge> challenges;
    private OnChallengeClickedListener onChallengeClickedListener;

    public ChallengeAdapter(OnChallengeClickedListener onChallengeClickedListener) {
        this.onChallengeClickedListener = onChallengeClickedListener;
    }

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
                stattitle="Zaakceptowany";
                break;
            case NOT_ACCEPTED:
                stattitle="Oczekuje na akceptację";
                break;
            case FINISHED:
                stattitle="Zakończony";
                break;
            case REJECTED:
                stattitle="Odrzucony";
                break;
            case NOT_CONFIRMED:
                stattitle="Oczekuje na potwierdzenie";
                break;


        }

        if (challenge.getScores() != null && (challenge.getChallengeStatus() == ChallengeStatus.NOT_CONFIRMED || challenge.getChallengeStatus() == ChallengeStatus.FINISHED)){
            viewHolder.tvFromScore.setText(Integer.toString(challenge.getScores().getFrom().getValue()));
            viewHolder.tvToScore.setText(Integer.toString(challenge.getScores().getTo().getValue()));
        }else {
            viewHolder.tvFromScore.setText("-");
            viewHolder.tvToScore.setText("-");
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
         View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}