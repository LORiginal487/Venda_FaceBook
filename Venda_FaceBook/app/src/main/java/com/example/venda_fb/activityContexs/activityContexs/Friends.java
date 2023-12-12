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
import com.example.venda_fb.activityContexs.Adapters.PeopleAdapter;
import com.example.venda_fb.activityContexs.Listeners.UserListener;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.example.venda_fb.activityContexs.utilities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Friends extends AppCompatActivity implements UserListener {
    List<String> documentNames = new ArrayList<>();
    FirebaseFirestore db;
    ManagePreferences managePreferences;
    RecyclerView recycleV;
    ProgressBar loadingProgressBar;
    TextView requests, suggestions, friends;
    List<User> userz = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        callViews();
        //thestarterMethod();
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());
    }
    public void addPostPage(View view) {
        Intent intent = new Intent(getApplicationContext(), AddaPost.class);
        startActivity(intent);
    }
    private void callViews(){
        loadingProgressBar = findViewById(R.id.progressBar);
        recycleV = findViewById(R.id.recycleVposts);
        requests = findViewById(R.id.Friend_Request);
        suggestions = findViewById(R.id.suggestionsBtn);
        friends = findViewById(R.id.friendsBtn2);
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
    public void profile(View view) {
        Intent intent = new Intent(getApplicationContext(), Profile.class);
        startActivity(intent);
    }

    public void menu(View view) {
        Intent intent = new Intent(getApplicationContext(), Menu.class);
        startActivity(intent);
    }


    public void openHome(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void showSuggests(View view) {
        loading(true);
        requests.setVisibility(View.VISIBLE);
        suggestions.setVisibility(View.GONE);
        friends.setVisibility(View.VISIBLE);
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Users);
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
                                    if (documentName.equals(queryDocumentSnapshot.getId())) {
                                        User user = new User();
                                        user.name = queryDocumentSnapshot.getString(Constants.Key_Name);
                                        user.surname = queryDocumentSnapshot.getString(Constants.Key_Surname);
                                        user.email = queryDocumentSnapshot.getString(Constants.Key_Email);
                                        user.image = queryDocumentSnapshot.getString(Constants.Key_Image);
                                        userz.add(user);
                                    }

                                }
                                if (userz.size() > 0) {
                                    PeopleAdapter peopleAdapter = new PeopleAdapter(userz,Constants.Key_Suggestion, Friends.this);
                                    recycleV.setAdapter(peopleAdapter);
                                    recycleV.setVisibility(View.VISIBLE);
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

    public void showRequests(View view) {
        requests.setVisibility(View.GONE);
        suggestions.setVisibility(View.VISIBLE);
        friends.setVisibility(View.VISIBLE);
    }

    public void openFriends(View view) {
        requests.setVisibility(View.VISIBLE);
        suggestions.setVisibility(View.VISIBLE);
        friends.setVisibility(View.GONE);
        thestarterMethod();
    }
    void thestarterMethod(){
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Users);
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
                                    if (documentName.equals(queryDocumentSnapshot.getId())) {
                                        User user = new User();
                                        user.name = queryDocumentSnapshot.getString(Constants.Key_Name);
                                        user.surname = queryDocumentSnapshot.getString(Constants.Key_Surname);
                                        user.email = queryDocumentSnapshot.getString(Constants.Key_Email);
                                        user.image = queryDocumentSnapshot.getString(Constants.Key_Image);
                                        user.token = queryDocumentSnapshot.getString(Constants.Key_FCM_Token);
                                        user.id = queryDocumentSnapshot.getId();
                                        userz.add(user);
                                    }

                                }
                                if (userz.size() > 0) {
                                    PeopleAdapter peopleAdapter = new PeopleAdapter(userz,Constants.Key_Friends, Friends.this);
                                    recycleV.setAdapter(peopleAdapter);
                                    recycleV.setVisibility(View.VISIBLE);
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

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(getApplicationContext(), User_Profile.class);
        intent.putExtra(Constants.Key_User, user);
        startActivity(intent);
        finish();
    }
}