package com.example.venda_fb.activityContexs.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Listeners.CommentListener;
import com.example.venda_fb.activityContexs.Listeners.RecentConvoClickerListener;
import com.example.venda_fb.activityContexs.utilities.ChatMessage;
import com.example.venda_fb.activityContexs.utilities.Comment;
import com.example.venda_fb.activityContexs.utilities.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.commentsViewHolder>{
    private final List<Comment> comments;
    private final CommentListener commentListener;

    public CommentsAdapter(List<Comment> comments, CommentListener commentListener ) {
        this.comments = comments;
        this.commentListener = commentListener;
    }

    @NonNull
    @Override
    public CommentsAdapter.commentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_layout, parent, false);
        return new commentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.commentsViewHolder holder, int position) {
        holder.setData(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    static class commentsViewHolder extends RecyclerView.ViewHolder {
        View itemView1;
        public commentsViewHolder(@NonNull View itemView) {

            super(itemView);
            itemView1 = itemView;
        }
        void setData(Comment comment){
            TextView name = itemView1.findViewById(R.id.name);
            name.setText(comment.commentNames);
            TextView time = itemView1.findViewById(R.id.recTimeTxt);
            time.setText(comment.commentTime);
            TextView text = itemView1.findViewById(R.id.incoming);
            text.setText(comment.commentTXT);
            RoundedImageView pp = itemView1.findViewById(R.id.commntaimgProfile);
            pp.setImageBitmap(getUserImage(comment.commentPP));

        }
        private Bitmap getUserImage(String encodedImg) {
            byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

    }
}
