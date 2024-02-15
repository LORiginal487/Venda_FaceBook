package com.example.venda_fb.activityContexs.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Listeners.LikesAndCommentListener;
import com.example.venda_fb.activityContexs.utilities.Post;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.UserViewHolder> {
    private final List<Post> post;
    private final LikesAndCommentListener likesAndCommentListener;

    public ProfileAdapter(List<Post> post, LikesAndCommentListener likesAndCommentListener) {
        this.likesAndCommentListener = likesAndCommentListener;
        this.post = post;
    }

    @NonNull
    @Override
    public ProfileAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.UserViewHolder holder, int position) {
        holder.setPostData(post.get(position));
    }

    @Override
    public int getItemCount() {
        return post.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

        }

        void setPostData(Post post) {
            TextView names = itemView.findViewById(R.id.posterName);
            names.setText(post.posterNames);
            TextView time = itemView.findViewById(R.id.timePosted);
            time.setText(post.postTime);
            TextView txt = itemView.findViewById(R.id.txtPOSTED);
            txt.setText(post.postTxt);
            RoundedImageView imageProfile = itemView.findViewById(R.id.posterPP);
            imageProfile.setImageBitmap(getBitmapFromBase64(post.posterPP));
            ImageView postedImage = itemView.findViewById(R.id.picture);
            postedImage.setImageBitmap(getBitmapFromBase64(post.postedPic));
            TextView likesC = itemView.findViewById(R.id.likesCount);
            likesC.setText(post.postLikes);
            TextView comntsC = itemView.findViewById(R.id.commentCount);
            comntsC.setText(post.postComments);
            TextView comntB = itemView.findViewById(R.id.commentBtn);
            TextView likeB = itemView.findViewById(R.id.likeBtn);
            comntB.setOnClickListener(v -> {

                likesAndCommentListener.onCommentClicked(post);
            });
            likeB.setOnClickListener(v -> {
                likesAndCommentListener.onLikeClicked(post);
            });
        }

        private Bitmap getBitmapFromBase64(String encodedImg) {
            byte[] decodedString = Base64.decode(encodedImg, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
    }
}