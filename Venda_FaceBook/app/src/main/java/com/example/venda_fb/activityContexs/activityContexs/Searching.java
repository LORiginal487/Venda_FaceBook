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
import com.example.venda_fb.activityContexs.Adapters.PeopleAdapter;
import com.example.venda_fb.activityContexs.Adapters.ProfileAdapter;
import com.example.venda_fb.activityContexs.Listeners.UserListener;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Searching extends AppCompatActivity implements UserListener {
    RoundedImageView imageViewPP;
    TextView nameV, bioV;
    EditText searchbar;
    User userDt;
    ManagePreferences managePreferences;
    ProgressBar loadingProgressBar;
    RecyclerView peopleView;
    DocumentReference docRef;
    FirebaseFirestore db;
    List<String> documentNames = new ArrayList<>();

    List<User> userz = new ArrayList<>();
    String peopleTshow, image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        //+++______--
        callViews();
        peopleTshow= "allPpl";
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());
        getAllDocumentNames();

    }
    private void callViews(){

        searchbar = findViewById(R.id.searchBar);
        peopleView = findViewById(R.id.recycleVsearch);
    }

    public void SearchNow(View view) {
    }
    private void getAllDocumentNames() {

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
                                    PeopleAdapter peopleAdapter = new PeopleAdapter(userz,Constants.Key_Every_Person, Searching.this);
                                    peopleView.setAdapter(peopleAdapter);
                                    peopleView.setVisibility(View.VISIBLE);

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

    @Override
    public void onUserClick(User user) {
        Log.d("1234567654321","--");
        Intent intent = new Intent(getApplicationContext(), User_Profile.class);
        Log.d("1234567654321","--=====");

        intent.putExtra(Constants.Key_User, user);
        Log.d("1234567654321","----------------"+user.name);
        startActivity(intent);
        finish();
    }
}