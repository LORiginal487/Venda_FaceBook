package com.example.venda_fb.activityContexs.activityContexs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Adapters.CommentsAdapter;
import com.example.venda_fb.activityContexs.Adapters.ProfileAdapter;
import com.example.venda_fb.activityContexs.Listeners.CommentListener;
import com.example.venda_fb.activityContexs.utilities.Comment;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommentsLayout extends AppCompatActivity implements CommentListener {
    TextView names, time,   text, likesCount, commentsCount;
    EditText writeComm;
    ImageView ImagePosted;
    ProgressBar progressBar;
    RecyclerView commentsRV;
    RoundedImageView imagePP;
    ManagePreferences managePreferences;
    FirebaseFirestore db;
    AppCompatImageView menuDots;
    LinearLayoutCompat likeBtnL;
    List<String> documentNames = new ArrayList<>();
    List<Comment> commntz = new ArrayList<>();
    private Post post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_layout);

        //**---------------
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());
        callViews();
        setEverything();
        showAllcomments();
    }
    private void callViews(){
        imagePP = findViewById(R.id.posterPP);
        names = findViewById(R.id.posterName);
        time = findViewById(R.id.timePosted);
        ImagePosted = findViewById(R.id.picture);
        text = findViewById(R.id.txtPOSTED);
        likesCount = findViewById(R.id.likesCount);
        commentsCount = findViewById(R.id.commentCount);
        writeComm = findViewById(R.id.typeBar);
        commentsRV = findViewById(R.id.recView);
        progressBar = findViewById(R.id.progressBar);
        menuDots = findViewById(R.id.dots);
         likeBtnL = findViewById(R.id.menuPanel);

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
        if(post.postedPic != null){
            Glide.with(CommentsLayout.this) // Pass the activity or fragment context
                    .load(post.postedPic) // Load the image from the URL
                    .into(ImagePosted);
        }else{
            ImagePosted.setVisibility(View.GONE);
        }
        if(!post.postID.contains(managePreferences.getString(Constants.Key_Email))){

            menuDots.setVisibility(View.GONE);
        }


    }
//    private String getRedableDate(Date date){
//        return new SimpleDateFormat(" HH:mm ", Locale.getDefault()).format(date);
//    }
    private Bitmap getBitmap(String imageStr){
        byte[] bytes = Base64.decode(imageStr, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    private void showAllcomments(){
        Log.d("ggggggggggggggggggggggggderggd", "0");
        getAllcomments();
        progressBar.setVisibility(View.GONE);
    }
    private void getAllcomments() {
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Comments);
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            commntz.clear(); // Clear the list before adding new comments
                            for (DocumentSnapshot document : task.getResult()) {
                                // Get the document ID and check if it exists in documentNames list
                                String documentName = document.getId();
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    if (documentName.equals(queryDocumentSnapshot.getId())) {
                                        Log.d("ggggggggggggggggggggggggderggd", "1");

                                        Comment commnt = new Comment();
                                        commnt.commentNames = queryDocumentSnapshot.getString(Constants.Key_Commenter_Name);
                                        commnt.commentTime = getRedableDate(queryDocumentSnapshot.getDate(Constants.Key_Commenter_TIME));
                                        commnt.commentTXT = queryDocumentSnapshot.getString(Constants.Key_Commenter_TXT);
                                        commnt.commentPP = queryDocumentSnapshot.getString(Constants.Key_Commenter_PP);
                                        commnt.postIDc = queryDocumentSnapshot.getString(Constants.Key_Post_ID_Comment);
                                        commnt.commentID = queryDocumentSnapshot.getString(Constants.Key_Comment_ID);
                                        Log.d("ggggggggggggggggggggggggderggd", "2" + commnt.commentID);
                                        if (commnt.postIDc.equals(post.postID)) {
                                            commntz.add(commnt);
                                            Log.d("ggggggggggggggggggggggggderggd", "3");
                                        }
                                    }
                                }
                            }

                            if (commntz.size() > 0) {
                                progressBar.setVisibility(View.GONE);
                                CommentsAdapter commentsAdapter = new CommentsAdapter(commntz, CommentsLayout.this);
                                commentsRV.setAdapter(commentsAdapter);

                                commentsRV.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);

                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private String getRedableDate(Date date){
        return new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()).format(date);
    }
    private void addingComments2db(String commenttxt){
        Map<String, Object> commi = new HashMap<>();
        commi.put(Constants.Key_Commenter_Name, managePreferences.getString(Constants.Key_Name));
        commi.put(Constants.Key_Commenter_PP, managePreferences.getString(Constants.Key_Image));
        commi.put(Constants.Key_Commenter_TIME, new Date());
        commi.put(Constants.Key_Commenter_TXT, commenttxt);
        commi.put(Constants.Key_Post_ID_Comment, post.postID);
        commi.put(Constants.Key_Comment_ID, post.postID+""+getRedableDate(new Date()));


        // Add a new document with a generated ID
        CollectionReference commCol = db.collection(Constants.Key_Collection_Comments);
        commCol.add(commi).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                getAllcomments();
                addAcommnt2db();
                sendNotification(Constants.Key_Comment_Not, extractEmail(post.postID), post.postID);
                // Call countTheLikes after successfully adding the like
                //getAllDocumentNames(post.postID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
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
                showToast("Commented!");


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
    private void addAcommnt2db(){
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
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the value of the existing field
                        String strCommm = document.getString(Constants.Key_Comments);

                        // Update the value or perform any operation
                        String addedAlyk = Integer.toString(Integer.parseInt(strCommm)+1); // Update this with your new value
                        updateField(documentRef, Constants.Key_Comments, addedAlyk);
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
                        Log.d("12345566666666666666", "===========---------");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("123455677777777777", "===========---------");
                    }
                });
    }


    public void addComment(View view) {
        String commentTxt = writeComm.getText().toString();
        addingComments2db(commentTxt);
        writeComm.setText(null);

    }

    @Override
    public void onCommentClicked(User user) {

    }
    private void showToast(String mssg){
        Toast.makeText(this, mssg, Toast.LENGTH_SHORT).show();
    }

}