package com.example.venda_fb.activityContexs.activityContexs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.example.venda_fb.activityContexs.utilities.Post;
import com.example.venda_fb.activityContexs.utilities.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends AppCompatActivity {
    private User userTo;
    RoundedImageView imagePP;
    TextView chatName, bioV;
    ImageView background;
    User userDt;
    ManagePreferences managePreferences;
    ProgressBar loadingProgressBar;
    RecyclerView chatRV;
    DocumentReference docRef;
    FirebaseFirestore db;
    List<String> documentNames = new ArrayList<>();

    List<Post> postz = new ArrayList<>();
    String fullName, image_pp,imageBG, userEm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        //_______------
        callViews();
        setEverything();
        loadChats();
    }
    private void loadChats(){
        loading(true);

    }
    private void setEverything(){
        //getIntent().getStringExtra(Constants.Key_Email)

        Log.d("1234567654321","----------------");
        fullName = getIntent().getStringExtra(Constants.Key_Name);
        Log.d("1234567654321","----------------");
        chatName.setText(fullName.toUpperCase());
        Log.d("1234567654321","----------------");
        Log.d("1234567654321", "12"+fullName);
        image_pp = getIntent().getStringExtra(Constants.Key_Image);
        imagePP.setImageBitmap(getBitmap(image_pp));
        userEm = getIntent().getStringExtra(Constants.Key_Email);

    }
    private void callViews(){
        imagePP = findViewById(R.id.imagePP);
        chatName = findViewById(R.id.chatName);

        chatRV = findViewById(R.id.recycleVchat);
        loadingProgressBar = findViewById(R.id.progressBar);
    }
    private Bitmap getBitmap(String imageStr){
        byte[] bytes = Base64.decode(imageStr, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public void go2inbox(View view) {
    }
    private void loading(boolean isLoading){
        Log.d("l3 1111111", "_______________");
        if(isLoading){

            loadingProgressBar.setVisibility(View.VISIBLE);
            Log.d("l4 1111111", "_______________");
        }else{

            loadingProgressBar.setVisibility(View.INVISIBLE);
            Log.d("l5 1111111", "_______________");
        }

    }
}