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

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.example.venda_fb.activityContexs.utilities.Post;
import com.example.venda_fb.activityContexs.Adapters.ProfileAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {
    RoundedImageView imageViewPP;
    TextView nameV, bioV;
    ImageView background;
    User userDt;
    ManagePreferences managePreferences;
    ProgressBar loadingProgressBar;
    RecyclerView myPostHistory;
    DocumentReference docRef;
    FirebaseFirestore db;
    List<String> documentNames = new ArrayList<>();

    List<Post> postz = new ArrayList<>();
    String fullName, image,imageBG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //---------------------
        callViews();
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());
        //getFromDB();
        setEverything();
        showPosts();

    }
    private void setEverything(){
        fullName = managePreferences.getString(Constants.Key_Name )+" "+managePreferences.getString(Constants.Key_Surname);
        nameV.setText(fullName.toUpperCase());
        Log.d("wwwwwwwwwwwwwww", "12"+fullName);
        imageViewPP.setImageBitmap(getBitmap(managePreferences.getString(Constants.Key_Image)));
        Log.d("wwwwwwwwwwwwwww", "123"+managePreferences.getString(Constants.Key_Image));
        if(managePreferences.getString(Constants.Key_Bio)!=null) {
            bioV.setText(managePreferences.getString(Constants.Key_Bio));
            Log.d("wwwwwwwwwwwwwww", "1234" + managePreferences.getString(Constants.Key_Bio));
        }
        Log.d("wwwwwwwwwwwwwww", "1234---");
        if(managePreferences.getString(Constants.Key_BackgroundPic)!=null) {
            background.setImageBitmap(getBitmap(managePreferences.getString(Constants.Key_BackgroundPic)));
            Log.d("wwwwwwwwwwwwwww", "12345====" + managePreferences.getString(Constants.Key_BackgroundPic));
        }
        Log.d("wwwwwwwwwwwwwww", "123456------");

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
        myPostHistory = findViewById(R.id.recView);
        loadingProgressBar = findViewById(R.id.progressBar);
    }
    private String getImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes= byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if(result.getResultCode()==RESULT_OK){
                    if(result.getData() != null){
                        Uri imgUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imgUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            background.setImageBitmap(bitmap);
                            imageBG = getImage(bitmap);
                            addInDb();
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

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
    private void addInDb(){
        showToast("Checking");

        // Reference to the Firebase database
        // Create a new user with a first and last name
        Map<String, Object> inBG = new HashMap<>();
        inBG.put(Constants.Key_BackgroundPic, imageBG);
        managePreferences.putString(Constants.Key_BackgroundPic, imageBG);
        Log.d("12345566777", "123456789"+managePreferences.getString(Constants.Key_BackgroundPic));

        // Add a new document with a generated ID
        CollectionReference usersCol = db.collection(Constants.Key_Collection_Users);

        usersCol.document(managePreferences.getString(Constants.Key_Email))
                .update(inBG).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast("Background updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Retry");
                    }
                });
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

    public void addBackGround(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    }
}