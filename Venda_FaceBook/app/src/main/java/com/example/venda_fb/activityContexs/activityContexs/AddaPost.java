package com.example.venda_fb.activityContexs.activityContexs;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class AddaPost extends AppCompatActivity {
    String fullName, image,imagePosted = "", postText;
    Boolean addImage = false;
    RoundedImageView imageViewPP;
    TextView nameV, addPict;
    EditText inpost;
    ConstraintLayout constrPict;
    ImageView pic2post;
     FirebaseFirestore db;
    ManagePreferences managePreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adda_post);


        //_________________--------------
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());
        callviews();
        setUp();

    }
    private void callviews(){
        imageViewPP = findViewById(R.id.posterPP);
        nameV = findViewById(R.id.posterName);
        inpost = findViewById(R.id.inPostText);
        addPict = findViewById(R.id.addPict);
        constrPict = findViewById(R.id.constrPict);
        pic2post = findViewById(R.id.picture);
    }
    private void setUp(){
        fullName = managePreferences.getString(Constants.Key_Name )+" "+managePreferences.getString(Constants.Key_Surname);
        nameV.setText(fullName.toUpperCase());
        image = managePreferences.getString(Constants.Key_Image);
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageViewPP.setImageBitmap(bitmap);
    }
    public void goHome(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void addPostNow(View view) throws InterruptedException {
        postText =inpost.getText().toString();
        if(!postText.isEmpty()){
            postAndSave();
        }
    }

    public void addPictures(View view) {
        //addPict.setVisibility(View.GONE);
        constrPict.setVisibility(View.VISIBLE);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
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
                            pic2post.setImageBitmap(bitmap);
                            imagePosted = getImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
    private String getImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes= byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private void postAndSave() throws InterruptedException {
        showToast("Checking");

        // Reference to the Firebase database
        // Create a new user with a first and last name
        Map<String, Object> post = new HashMap<>();
        post.put(Constants.Key_P_Names, fullName);
        post.put(Constants.Key_P_Text, postText);
        post.put(Constants.Key_Image_pp, managePreferences.getString(Constants.Key_Image));
        post.put(Constants.Key_Picture, imagePosted);
        post.put(Constants.Key_Post_Time, "00:00");
        post.put(Constants.Key_Likes, "0");
        post.put(Constants.Key_Comments, "0");
        Log.d("12345566777", "123456789"+managePreferences.getString(Constants.Key_Image));

        // Add a new document with a generated ID
        CollectionReference usersCol = db.collection(Constants.Key_Collection_Posts);

        String docName = managePreferences.getString(Constants.Key_Email)+""+1;
        usersCol.document(docName).set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("Posted!");
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
    private long countDocuments()
            throws InterruptedException{
        // Reference to the collection
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Posts);

        // Initialize a CountDownLatch with a count of 1
        CountDownLatch latch = new CountDownLatch(1);

        // Variable to store the document count
        long[] documentCount = {0};

        // Get the documents in the collection
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null) {
                    // Count the number of documents
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        documentCount[0]++;
                        Log.d("12345566777", "_________________"+documentCount[0]);
                        // You can also access data from each document if needed
                        // For example: String documentId = document.getId();
                        //             Map<String, Object> data = document.getData();
                    }
                }
            } else {
                // Handle the case when the operation is not successful
                task.getException().printStackTrace();
            }

            // Release the latch, signaling that the operation is complete
            latch.countDown();
        });

        // Wait for the asynchronous operation to complete
        latch.await();

        return documentCount[0];
    }

    private void showToast(String mssg){
        Toast.makeText(this, mssg, Toast.LENGTH_SHORT).show();
    }

}