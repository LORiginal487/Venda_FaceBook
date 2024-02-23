package com.example.venda_fb.activityContexs.activityContexs;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class User_Profile extends AppCompatActivity implements LikesAndCommentListener {
    private User userTo;
    RoundedImageView imageViewPP;
    TextView nameV, bioV;
    ImageView background;
    User userDt;
    ManagePreferences managePreferences;
    ProgressBar loadingProgressBar;
    RecyclerView PostHistory;
    DocumentReference docRef;
    FirebaseFirestore db;
    List<String> documentNames = new ArrayList<>();

    List<Post> postz = new ArrayList<>();
    int postnum = 1;
    String docName;
    String fullName, image_pp,imageBG, userEm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //---------------------
        callViews();
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());
        Log.d("1234567654321","----------------");
        //getFromDB();
        setEverything();
        showPosts();
    }


    private void setEverything(){
        //getIntent().getStringExtra(Constants.Key_Email)
        userTo =(User) getIntent().getSerializableExtra(Constants.Key_User);
        Log.d("1234567654321","----------------");
        fullName = userTo.name +" " + userTo.surname;
        Log.d("1234567654321","----------------");
        nameV.setText(fullName.toUpperCase());
        Log.d("1234567654321","----------------");
        Log.d("1234567654321", "12"+fullName);
        image_pp = userTo.image;
        imageViewPP.setImageBitmap(getBitmap(image_pp));
        if(userTo.biot !=null){
            bioV.setText(userTo.biot);
        }
        if(userTo.bckGnd != null){
            showNewImage(userTo.bckGnd, background);
        }


    }
    private Bitmap getBitmap(String imageStr){
        byte[] bytes = Base64.decode(imageStr, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    private void callViews(){
        background = findViewById(R.id.imageBG);
        imageViewPP = findViewById(R.id.myPP);
        nameV = findViewById(R.id.nameDis);
        bioV = findViewById(R.id.Bio);
        PostHistory = findViewById(R.id.recView);
        loadingProgressBar = findViewById(R.id.progressBar);
    }
    private void showNewImage(String imageU, ImageView imageView){
        Glide.with(User_Profile.this) // Pass the activity or fragment context
                .load(imageU) // Load the image from the URL
                .into(imageView);
    }
    private void showPosts(){

        getAllDocumentNames();

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
                                    if (documentName.equals(queryDocumentSnapshot.getId())
                                            &&documentName.contains(userTo.email)) {
                                        Post post = new Post();
                                        post.posterNames = queryDocumentSnapshot.getString(Constants.Key_P_Names);
                                        post.postTime = getRedableDate(queryDocumentSnapshot.getDate(Constants.Key_Post_Time));
                                        post.postTxt = queryDocumentSnapshot.getString(Constants.Key_P_Text);
                                        post.posterPP = queryDocumentSnapshot.getString(Constants.Key_Image_pp);
                                        post.postedPic = queryDocumentSnapshot.getString(Constants.Key_Picture);
                                        post.postLikes = queryDocumentSnapshot.getString(Constants.Key_Likes);
                                        post.postID = queryDocumentSnapshot.getString(Constants.Key_P_ID);
                                        post.postComments = queryDocumentSnapshot.getString(Constants.Key_Comments);

                                        postz.add(post);
                                    }

                                }
                                if (postz.size() > 0) {
                                    ProfileAdapter profileAdapter = new ProfileAdapter(postz, User_Profile.this);
                                    PostHistory.setAdapter(profileAdapter);
                                    PostHistory.setVisibility(View.VISIBLE);
                                    loading(false);
                                }

                            }


                            // Now you have a list of all document names
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        loading(false);

    }
    private String getRedableDate(Date date){
        return new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()).format(date);
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

    private void showToast(String mssg){
        Toast.makeText(this, mssg, Toast.LENGTH_SHORT).show();
    }
    //___________________________________-----------------------------
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

    public void addBio(View view) {
        Intent intent = new Intent(getApplicationContext(), Add_Bio.class);
        startActivity(intent);
    }


    public void openChat(View view) {
        Intent intent = new Intent(getApplicationContext(), ChatScreen.class);
        intent.putExtra(Constants.Key_User, userTo);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCommentClicked(Post post) {
        Log.d("l6 1111111", "_______________");
        Intent intent = new Intent(getApplicationContext(), CommentsLayout.class);
        // Pass any necessary data to the CommentsLayout activity using extras
        intent.putExtra(Constants.Key_Post, post);
        startActivity(intent);
    }

    @Override
    public void onLikeClicked(Post post) {
        addAlike2db(post);
        likingApost(post);
    }
    private void addAlike2db(Post post){
        // Reference to the collection
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Posts);
        String docname2 = null;
        for (int i = 0; i < post.postID.length(); i++){
            int endIndex = post.postID.indexOf("post");

            // Extract the substring before "team"
            docname2 = post.postID.substring(0, endIndex).trim();

            // Add the extracted name to the list

        }
        // Reference to the document you want to update
        DocumentReference documentRef = collectionRef.document(docname2);

        // Fetch the document
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the value of the existing field
                        String strLike = document.getString(Constants.Key_Comments);

                        // Update the value or perform any operation
                        String addedAlyk = Integer.toString(Integer.parseInt(strLike)+1); // Update this with your new value
                        updateField(documentRef, Constants.Key_Likes, addedAlyk);
                    } else {

                    }
                } else {

                }
            }
        });
    }
    private void updateField(DocumentReference documentRef, String fieldName, String newValue) {
        // Create a map to update the field
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(fieldName, newValue);

        // Perform the update
        documentRef.update(updateMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("12345566666666666666", "===========---------" + docName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("123455677777777777", "===========---------" + docName);
                    }
                });
    }
    private void likingApost(Post post){
        // Reference to the Firebase database
        // Create a new user with a first and last name
        Map<String, Object> alike = new HashMap<>();
        alike.put(Constants.Key_Liker_Name, managePreferences.getString(Constants.Key_Name));
        alike.put(Constants.Key_Post_ID_like, post.postID);

        // Add a new document with a generated ID
        CollectionReference likesCol = db.collection(Constants.Key_Collection_Likes);
        likesCol.add(alike).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                showToast("Liked!");
                sendNotification(Constants.Key_Like_Noti, extractEmail(post.postID), post.postID);
                // Call countTheLikes after successfully adding the like
                //getAllDocumentNames(post.postID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Retry!");
            }
        });
    }
    private String extractEmail(String postId) {
        String[] email1 = postId.split("-");
        return email1[0];



    }
    private void sendNotification(String type, String email, String postID){
        Map<String, Object> noti = new HashMap<>();
        noti.put(Constants.Key_Noti_Names, managePreferences.getString(Constants.Key_Name));
        noti.put(Constants.Key_Noti_pp, managePreferences.getString(Constants.Key_Image));
        noti.put(Constants.Key_Noti_Text, type);
        noti.put(Constants.Key_Noti_PostId, postID);
        noti.put(Constants.Key_Noti_Time, new Date());
        noti.put(Constants.Key_Noti_Email, email);
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Notifications);
        collectionRef.add(noti).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                showToast("Liked!");


                // Call countTheLikes after successfully adding the like
                //getAllDocumentNames(post.postID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Retry!");
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

    @Override
    public void onPictureClick(Post post) {
        Intent intent = new Intent(this, View_Image.class);
        // Pass any necessary data to the CommentsLayout activity using extras
        intent.putExtra(Constants.Key_Post, post);
        startActivity(intent);
    }

    @Override
    public void onPersonClicked(User user) {

    }
}