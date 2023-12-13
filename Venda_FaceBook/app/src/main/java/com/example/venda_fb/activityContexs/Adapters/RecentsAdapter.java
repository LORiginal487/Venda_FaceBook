package com.example.venda_fb.activityContexs.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Listeners.UserListener;
import com.example.venda_fb.activityContexs.utilities.User;

import java.util.List;

public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.RecentsViewHolder> {
    private final List<User> users;
    private final UserListener userL;

    public RecentsAdapter(List<User> users, UserListener userL) {
        this.userL = userL;
        this.users = users;
    }
    @NonNull
    @Override
    public RecentsAdapter.RecentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chats, parent, false);
        return new RecentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentsAdapter.RecentsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RecentsViewHolder extends RecyclerView.ViewHolder {
        public RecentsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
