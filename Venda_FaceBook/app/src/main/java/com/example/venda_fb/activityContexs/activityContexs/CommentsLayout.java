package com.example.venda_fb.activityContexs.activityContexs;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.Post;
import com.example.venda_fb.activityContexs.utilities.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommentsLayout extends AppCompatActivity {
    TextView names, time,   text, likesCount, commentsCount;
    ImageView ImagePosted;
    RoundedImageView imagePP;
    private Post post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_layout);

        //**---------------
        callViews();
        setEverything();
    }
    private void callViews(){
        imagePP = findViewById(R.id.posterPP);
        names = findViewById(R.id.posterName);
        time = findViewById(R.id.timePosted);
        ImagePosted = findViewById(R.id.picture);
        text = findViewById(R.id.txtPOSTED);
        likesCount = findViewById(R.id.likesCount);
        commentsCount = findViewById(R.id.commentCount);

    }
    private void setEverything(){
        //getIntent().getStringExtra(Constants.Key_Email)
        post = (Post) getIntent().getSerializableExtra(Constants.Key_Post);
        names.setText(post.posterNames);
        text.setText(post.postTxt);
        likesCount.setText(post.postLikes);
        commentsCount.setText(post.postComments);
        time.setText(post.postTime);
        imagePP.setImageBitmap(getBitmap(post.posterPP));
        ImagePosted.setImageBitmap(getBitmap(post.postedPic));


    }
//    private String getRedableDate(Date date){
//        return new SimpleDateFormat(" HH:mm ", Locale.getDefault()).format(date);
//    }
    private Bitmap getBitmap(String imageStr){
        byte[] bytes = Base64.decode(imageStr, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}