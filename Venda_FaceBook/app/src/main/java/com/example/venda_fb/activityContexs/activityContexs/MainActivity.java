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
import android.widget.Toast;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Adapters.ProfileAdapter;
import com.example.venda_fb.activityContexs.Listeners.LikesAndCommentListener;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.example.venda_fb.activityContexs.utilities.Post;
import com.example.venda_fb.activityContexs.utilities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LikesAndCommentListener {

    ProgressBar loadingProgressBar;
    RecyclerView feedPosts;
    DocumentReference docRef;
    FirebaseFirestore db;
    List<String> documentNames = new ArrayList<>();
    ManagePreferences managePreferences;
    List<Post> postz = new ArrayList<>();
    String docName;
    int postnum = 1;
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

                                    post.postTime = getRedableDate(queryDocumentSnapshot.getDate(Constants.Key_Post_Time));

                                    post.postTxt = queryDocumentSnapshot.getString(Constants.Key_P_Text);
                                    post.posterPP = queryDocumentSnapshot.getString(Constants.Key_Image_pp);
                                    post.postedPic = queryDocumentSnapshot.getString(Constants.Key_Picture);
                                    post.postLikes = queryDocumentSnapshot.getString(Constants.Key_Likes);
                                    post.postComments = queryDocumentSnapshot.getString(Constants.Key_Comments);
                                    post.postID = queryDocumentSnapshot.getString(Constants.Key_P_ID);
                                    postz.add(post);
                                }
                                if (postz.size() > 0) {

                                    Collections.shuffle(postz);
                                    ProfileAdapter profileAdapter = new ProfileAdapter(postz, MainActivity.this);
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
    private String getRedableDate(Date date){
        return new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()).format(date);
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

    @Override
    public void onCommentClicked(Post post) {
        Log.d("l6 1111111", "_______________");
        Intent intent = new Intent(getApplicationContext(), CommentsLayout.class);
        // Pass any necessary data to the CommentsLayout activity using extras
        intent.putExtra(Constants.Key_Post, post);
        startActivity(intent);

    }
    private void showToast(String mssg){
        Toast.makeText(this, mssg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLikeClicked(Post post) {

    }
    private void likingApost(String postID){


        // Reference to the Firebase database
        // Create a new user with a first and last name
        Map<String, Object> alike = new HashMap<>();
        alike.put(Constants.Key_Liker_Name, managePreferences.getString(Constants.Key_Name));
        alike.put(Constants.Key_Post_ID_like, postID);

        // Add a new document with a generated ID
        CollectionReference usersCol = db.collection(Constants.Key_Collection_Likes);

        usersCol.document(docName).set(alike).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //loading(false);
                showToast("Retry!");
            }
        });
    }
    private void getAllDocumentNames( String postID) {
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Likes);

        collectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> documentNames = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                // Get the document ID (name) and add it to the list
                                String documentName = document.getId();
                                documentNames.add(documentName);
                            }

                            // Now you have a list of all document names
                            countTheLikes(documentNames, postID);

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void countTheLikes(List<String> documentNames, String postID){
        docName=postID+"likes";

        for(int i=0;i < documentNames.size(); i++){
            String namesss = documentNames.get(i);
            if(namesss.contains(postID)) {
                postnum ++;
            }
        }
        docName = docName + "-" + postnum;
        Log.d("12345566", "===========---------" + docName);

    }

}