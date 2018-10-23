package com.mmc.fifulec.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mmc.fifulec.R;
import com.mmc.fifulec.activity.Closeable;
import com.mmc.fifulec.model.OnUserClickedListener;
import com.mmc.fifulec.model.OpponentSelected;
import com.mmc.fifulec.model.User;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private List<User> users;
    private OnUserClickedListener onUserClickedListener;
    private Closeable closable;
    private Context context;

    public UserListAdapter(OnUserClickedListener onUserClickedListener) {
        this.onUserClickedListener = onUserClickedListener;
    }

    public UserListAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final User user = users.get(i);
        viewHolder.tvNick.setText(user.getNick());

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                if (onUserClickedListener != null) {
                                    onUserClickedListener.onUserSelect(new OpponentSelected(user, true));
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                if (onUserClickedListener != null){
                                    onUserClickedListener.onUserSelect(new OpponentSelected(user, false));
                                }
                                break;
                        }
                        closable.close();
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder
                        .setMessage("Czy chcesz rozegrać także rewanż?")
                        .setPositiveButton("Tak", dialogClickListener)
                        .setNegativeButton("Nie", dialogClickListener).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setOnUserClickedListener(OnUserClickedListener onUserClickedListener) {
        this.onUserClickedListener = onUserClickedListener;
    }

    public void setClosable(Closeable closable) {
        this.closable = closable;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_user_nick)
        TextView tvNick;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
