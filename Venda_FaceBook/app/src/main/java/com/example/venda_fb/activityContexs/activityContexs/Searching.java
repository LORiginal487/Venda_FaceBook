package com.example.venda_fb.activityContexs.activityContexs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.util.List;

public class Searching extends AppCompatActivity {
    RoundedImageView imageViewPP;
    TextView nameV, bioV;
    EditText searchbar;
    User userDt;
    ManagePreferences managePreferences;
    ProgressBar loadingProgressBar;
    RecyclerView myPostHistory;
    DocumentReference docRef;
    FirebaseFirestore db;
    List<String> documentNames = new ArrayList<>();

    List<Post> postz = new ArrayList<>();
    String fullName, image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        //+++______--
        callViews();
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());

    }
    private void callViews(){

        searchbar = findViewById(R.id.searchBar);
    }

    public void SearchNow(View view) {
    }
    private void getAllDocumentNames() {

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
                                    if (documentName.equals(queryDocumentSnapshot.getId())
                                            &&documentName.contains(managePreferences.getString(Constants.Key_Email))) {
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

                                }
                                if (postz.size() > 0) {
                                    ProfileAdapter profileAdapter = new ProfileAdapter(postz);
                                    myPostHistory.setAdapter(profileAdapter);
                                    myPostHistory.setVisibility(View.VISIBLE);

                                }

                            }


                            // Now you have a list of all document names
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void goHome(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}