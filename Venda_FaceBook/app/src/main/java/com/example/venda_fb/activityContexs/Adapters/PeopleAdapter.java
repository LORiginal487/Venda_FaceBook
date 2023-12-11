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
import com.example.venda_fb.activityContexs.Listeners.UserListener;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.Post;
import com.example.venda_fb.activityContexs.utilities.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;
import java.util.Objects;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.UserViewHolder>{
    private final List<User> users;
    String peopleToShow;
    UserListener userL;

    public PeopleAdapter(List<User> users, String peopleToShow, UserListener userL) {
        this.users = users;
        this.peopleToShow =peopleToShow;
        this.userL = userL;
    }

    @NonNull
    @Override
    public PeopleAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(peopleToShow == Constants.Key_Every_Person){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people, parent, false);
            return new UserViewHolder(view);
        } else if (peopleToShow == Constants.Key_Suggestion) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_sugg_list, parent, false);
            return new UserViewHolder(view);
        }else if (peopleToShow == Constants.Key_Friends) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list, parent, false);
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
                itemView.setOnClickListener(v -> userL.onUserClick(user));
            if(Objects.equals(peopleToShow, Constants.Key_Every_Person)){
                TextView names = itemView.findViewById(R.id.posterName);
                names.setText((user.name +" "+ user.surname).toUpperCase());

                TextView email = itemView.findViewById(R.id.email);
                email.setText(user.email);

                RoundedImageView imageProfile = itemView.findViewById(R.id.image);
                imageProfile.setImageBitmap(getUserImage(user.image));

            }
            else {
                    TextView names = itemView.findViewById(R.id.theirName);
                    names.setText((user.name +" "+ user.surname).toUpperCase());

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
