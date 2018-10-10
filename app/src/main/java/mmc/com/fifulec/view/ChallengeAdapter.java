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
        viewHolder.tvAccepted.setText(challenge.isAccepted() ? "Zaakceptowano" : "Nie zaakceptowano");

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
         View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}