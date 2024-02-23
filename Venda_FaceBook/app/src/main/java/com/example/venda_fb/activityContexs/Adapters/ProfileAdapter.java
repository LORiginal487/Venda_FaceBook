package com.example.venda_fb.activityContexs.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Listeners.LikesAndCommentListener;
import com.example.venda_fb.activityContexs.activityContexs.Profile;
import com.example.venda_fb.activityContexs.utilities.ConstantMethods;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.Post;
import com.example.venda_fb.activityContexs.utilities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.UserViewHolder> {
    private final List<Post> post;
    private final LikesAndCommentListener likesAndCommentListener;
    FirebaseFirestore db;
    User user1;

    List<String> documentNames = new ArrayList<>();

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
            if(post.postedPic != null){
                Glide.with(itemView.getContext()) // Pass the activity or fragment context
                        .load(post.postedPic) // Load the image from the URL
                        .into(postedImage);
            }else{
                postedImage.setVisibility(View.GONE);
            }

            TextView likesC = itemView.findViewById(R.id.likesCount);
            likesC.setText(post.postLikes);
            TextView comntsC = itemView.findViewById(R.id.commentCount);
            LinearLayoutCompat likeBtnL = itemView.findViewById(R.id.likeBtn);
            LinearLayoutCompat commBtnL = itemView.findViewById(R.id.commentBtn);
            comntsC.setText(post.postComments);
                TextView comntB = itemView.findViewById(R.id.commBtnt);
            TextView likeB = itemView.findViewById(R.id.likeBtnt);
            commBtnL.setOnClickListener(v -> {
                likesAndCommentListener.onCommentClicked(post);
            });
            comntB.setOnClickListener(v -> {
                likesAndCommentListener.onCommentClicked(post);
            });
            likeB.setOnClickListener(v -> {
                likesAndCommentListener.onLikeClicked(post);
            });
            postedImage.getRootView().setOnClickListener(v -> {
                likesAndCommentListener.onPictureClick(post);
                    }
            );
            likeBtnL.setOnClickListener(v -> {
                likesAndCommentListener.onLikeClicked(post);
            });
            names.setOnClickListener(v -> {
                //extractEmail(post.postID);
                ConstantMethods.getUserByEmail(extractEmail(post.postID), new ConstantMethods.OnUserRetrievedListener() {
                    @Override
                    public void onUserRetrieved(User user) {
                        likesAndCommentListener.onPersonClicked(user);
                    }
                });
                Log.d("wertyioitrfhhnlk", "2345678988888"+post.postID);
            });
            imageProfile.setOnClickListener(v -> {
                Log.d("wertyioitrfhhnlk", "2345678988888"+post.postID);

                ConstantMethods.getUserByEmail(extractEmail(post.postID), new ConstantMethods.OnUserRetrievedListener() {
                    @Override
                    public void onUserRetrieved(User user) {
                        likesAndCommentListener.onPersonClicked(user);
                    }
                });
            });
            postedImage.setOnClickListener(v -> {
                ConstantMethods.getPostByID(post.postID, new ConstantMethods.OnPostRetrievedListener() {
                    @Override
                    public void onPostRetrieved(Post post) {
                        likesAndCommentListener.onPictureClick(post);
                    }
                });
            });
        }

        private Bitmap getBitmapFromBase64(String encodedImg) {
            if(encodedImg != null && encodedImg != "null" && encodedImg != "" && !encodedImg.isEmpty() && encodedImg.length()>5) {
                byte[] decodedString = Base64.decode(encodedImg, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }else{
                return null;
            }
        }
        private String extractEmail(String name) {


            String[] parts = name.split("-");
            String email = parts[0];


            return email;
        }private void clickPicture(String postId){
            ConstantMethods.getPostByID(postId, new ConstantMethods.OnPostRetrievedListener() {
                @Override
                public void onPostRetrieved(Post post) {
                    likesAndCommentListener.onPictureClick(post);
                }
            });
        }
        private void clickComment(String postId){
            ConstantMethods.getPostByID(postId, new ConstantMethods.OnPostRetrievedListener() {
                @Override
                public void onPostRetrieved(Post post) {
                    likesAndCommentListener.onCommentClicked(post);
                }
            });
        }
        private void clickLike(String postId){
            ConstantMethods.getPostByID(postId, new ConstantMethods.OnPostRetrievedListener() {
                @Override
                public void onPostRetrieved(Post post) {
                    likesAndCommentListener.onLikeClicked(post);
                }
            });
        }

    }
}