package com.example.venda_fb.activityContexs.activityContexs;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.Post;

public class View_Image extends AppCompatActivity {
    TextView text;
    ImageView image;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        //********_-----------
        callViews();
        setEverything();
    }
    private void callViews() {
        text = findViewById(R.id.text);
        image = findViewById(R.id.imageShow);
    }
    private void setEverything(){
        post = (Post) getIntent().getSerializableExtra(Constants.Key_Post);
        text.setText(post.postTxt);
        image.setImageBitmap(getBitmap(post.postedPic));

    }
    private Bitmap getBitmap(String imageStr){
        byte[] bytes = Base64.decode(imageStr, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}