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
import com.example.venda_fb.activityContexs.utilities.Post;
import com.example.venda_fb.activityContexs.utilities.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.UserViewHolder>{
    private final List<User> users;
    String peopleToShow;

    public PeopleAdapter(List<User> users, String peopleToShow) {
        this.users = users;
        this.peopleToShow =peopleToShow;
    }

    @NonNull
    @Override
    public PeopleAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(peopleToShow == "allPpl"){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people, parent, false);
            return new UserViewHolder(view);
        }
        else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleAdapter.UserViewHolder holder, int position) {
        holder.setPersonData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
            void setPersonData(User user) {
            if(peopleToShow == "allPpl"){
                TextView names = itemView.findViewById(R.id.posterName);
                names.setText((user.name +" "+ user.surname).toUpperCase());
                //posterPP, postedPic, postTime, postTxt, postLikes, postComments;
                TextView email = itemView.findViewById(R.id.email);
                email.setText(user.email);

                RoundedImageView imageProfile = itemView.findViewById(R.id.image);
                imageProfile.setImageBitmap(getUserImage(user.image));

            }
            /*TextView names = itemView.findViewById(R.id.posterName);
            names.setText(post.posterNames);
            //posterPP, postedPic, postTime, postTxt, postLikes, postComments;
            TextView time = itemView.findViewById(R.id.timePosted);
            time.setText(post.postTime);
            TextView txt = itemView.findViewById(R.id.txtPOSTED);
            txt.setText(post.postTxt);
            RoundedImageView imageProfile = itemView.findViewById(R.id.posterPP);
            imageProfile.setImageBitmap(getUserImage(post.posterPP));
            ImageView postedImage = itemView.findViewById(R.id.picture);
            postedImage.setImageBitmap(getUserImage(post.postedPic));
            TextView likesC = itemView.findViewById(R.id.likesCount);
            likesC.setText(post.postLikes);
            TextView comntsC = itemView.findViewById(R.id.commentCount);
            comntsC.setText(post.postComments);*/
        }
        private Bitmap getUserImage(String encodedImg) {
            byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }
}
