package com.example.venda_fb.activityContexs.activityContexs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Adapters.ProfileAdapter;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.example.venda_fb.activityContexs.utilities.Post;
import com.example.venda_fb.activityContexs.utilities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ProgressBar loadingProgressBar;
    RecyclerView feedPosts;
    DocumentReference docRef;
    FirebaseFirestore db;
    List<String> documentNames = new ArrayList<>();
    ManagePreferences managePreferences;
    List<Post> postz = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //+____________________
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());
        callViews();
        getAllDocumentNames();
    }
    private void callViews(){
        feedPosts = findViewById(R.id.recycleVposts);
        loadingProgressBar = findViewById(R.id.progressBar);
    }
    private void getAllDocumentNames() {
        loading(true);
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Posts);
        collectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Get the document ID (name) and add it to the list
                                String documentName = document.getId();
                                documentNames.add(documentName);

                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    if (documentName.equals(queryDocumentSnapshot.getId())){
                                       continue;
                                    }
                                    Post post = new Post();
                                    post.posterNames = queryDocumentSnapshot.getString(Constants.Key_P_Names);
                                    post.postTime = queryDocumentSnapshot.getString(Constants.Key_Post_Time);
                                    post.postTxt = queryDocumentSnapshot.getString(Constants.Key_P_Text);
                                    post.posterPP = queryDocumentSnapshot.getString(Constants.Key_Image_pp);
                                    post.postedPic = queryDocumentSnapshot.getString(Constants.Key_Picture);
                                    post.postLikes = queryDocumentSnapshot.getString(Constants.Key_Likes);
                                    post.postComments = queryDocumentSnapshot.getString(Constants.Key_Comments);
                                    postz.add(post);
                                }
                                if (postz.size() > 0) {

                                    Collections.shuffle(postz);
                                    ProfileAdapter profileAdapter = new ProfileAdapter(postz);
                                    feedPosts.setAdapter(profileAdapter);
                                    feedPosts.setVisibility(View.VISIBLE);
                                    loading(false);
                                }

                            }


                            // Now you have a list of all document names
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

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

    public void addPostPage(View view) {
        Intent intent = new Intent(getApplicationContext(), AddaPost.class);
        startActivity(intent);
    }

    public void search(View view) {
        Intent intent = new Intent(getApplicationContext(), Searching.class);
        startActivity(intent);
    }

    public void inbox(View view) {
        Intent intent = new Intent(getApplicationContext(), Inbox.class);
        startActivity(intent);
    }

    public void friendsList(View view) {
        Intent intent = new Intent(getApplicationContext(), Friends.class);
        startActivity(intent);
    }

    public void notifications(View view) {
        Intent intent = new Intent(getApplicationContext(), Notification.class);
        startActivity(intent);
    }

    public void menu(View view) {
        Intent intent = new Intent(getApplicationContext(), Menu.class);
        startActivity(intent);
    }


    public void profile(View view) {
        Intent intent = new Intent(getApplicationContext(), Profile.class);
        startActivity(intent);
    }
}