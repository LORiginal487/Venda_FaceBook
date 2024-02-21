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
import androidx.recyclerview.widget.RecyclerView;

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
            if(getBitmapFromBase64(post.postedPic) != null) {
                postedImage.setImageBitmap(getBitmapFromBase64(post.postedPic));
            }else{
                postedImage.setVisibility(View.GONE);
            }
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
            names.setOnClickListener(v -> {
                //extractEmail(post.postID);
                Log.d("wertyioitrfhhnlk", "2345678988888"+post.postID);
            });
            imageProfile.setOnClickListener(v -> {
                Log.d("wertyioitrfhhnlk", "2345678988888"+post.postID);

                //extractEmail(post.postID);
            });
            postedImage.setOnClickListener(v -> {
                likesAndCommentListener.onPictureClick(post);
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
        private void extractEmail(String postId) {
            String[] email1 = postId.split("-");
            String email = email1[0];
            Log.d("wertyioitrfhhnlk", "2345678988888"+email);

            extractUser(email);

        }
        private void extractUser(String email) {
            db.collection(Constants.Key_Collection_Users).whereEqualTo(Constants.Key_Email, email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                Log.d("wertyioitrfhhnlk", "2345678988888"+user.email);

                                likesAndCommentListener.onPersonClicked(user);
                            }
                        } else {
                            Log.d("TAG", "Error getting user by email: ", task.getException());
                        }
                    });
        }
    }
}