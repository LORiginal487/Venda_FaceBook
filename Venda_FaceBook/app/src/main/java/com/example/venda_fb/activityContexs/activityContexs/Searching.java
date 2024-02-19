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
import com.example.venda_fb.activityContexs.utilities.ConstantMethods;
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

        ConstantMethods.getAllUsers(peopleView, Searching.this);

    }

    public void goHome(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this, Profile.class);
        // Pass any necessary data to the CommentsLayout activity using extras
        intent.putExtra(Constants.Key_User, user);
        startActivity(intent);
    }

    @Override
    public void OnAddFriendClick(User user) {

    }
    private void addingAfriend(User user){

    }
}