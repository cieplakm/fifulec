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
import mmc.com.fifulec.model.Challange;

public class ChalangeAdapter extends RecyclerView.Adapter<ChalangeAdapter.ViewHolder> {

    private List<Challange> challanges;



    @NonNull
    @Override
    public ChalangeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, viewGroup, false);
        return new ChalangeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChalangeAdapter.ViewHolder viewHolder, int i) {
        Challange challange = challanges.get(i);
        viewHolder.tvFromUser.setText(challange.getFromUserUuid());
        viewHolder.tvToUser.setText(challange.getToUserUuid());
    }

    @Override
    public int getItemCount() {
        return challanges != null ? challanges.size() : 0;
    }

    public void setChallanges(List<Challange> challanges) {
        this.challanges = challanges;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_from_user_nick)
        TextView tvFromUser;
        @BindView(R.id.tv_to_user_nick)
        TextView tvToUser;
         View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}