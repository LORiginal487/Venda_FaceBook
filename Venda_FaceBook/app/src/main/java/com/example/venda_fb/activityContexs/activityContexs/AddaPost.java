package com.example.venda_fb.activityContexs.activityContexs;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

public class AddaPost extends AppCompatActivity {
    String fullName, image,imagePosted, postText,docName, email;
    Boolean addImage = false;
    RoundedImageView imageViewPP;
    TextView nameV, addPict;
    EditText inpost;
    ConstraintLayout constrPict;
    ImageView pic2post;
    int postnum = 1;
     FirebaseFirestore db;
    ManagePreferences managePreferences;
    StorageReference storageReference;
    FirebaseStorage fbStorage;
    ArrayList<String> urlList;
    FirebaseStorage storage1;
    Uri imageUri2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adda_post);


        //_________________--------------
        db = FirebaseFirestore.getInstance();
        fbStorage = FirebaseStorage.getInstance();
        storage1 = FirebaseStorage.getInstance();
        storageReference = fbStorage.getReference();
        managePreferences = new ManagePreferences(getApplicationContext());
        getAllDocumentNames();

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
        email = managePreferences.getString(Constants.Key_Email );
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
                        imageUri2 =imgUri;
                        addImage = true;
                        //newPicSaver(imgUri);
                        uploadAnImage(imgUri);

                        Log.d("ttrttrttrtrttrttrtrtrt", "123456789-0---"+imageUri2);
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imgUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            pic2post.setImageBitmap(bitmap);
                        }catch (FileNotFoundException e){
                            showToast("Retry!");
                        }
//
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


    private void postAndSave() {
        showToast("Checking");

        // Reference to the Firebase database
        // Create a new user with a first and last name
        Map<String, Object> post = new HashMap<>();
        post.put(Constants.Key_P_Names, fullName);
        post.put(Constants.Key_P_Text, postText);
        post.put(Constants.Key_Image_pp, managePreferences.getString(Constants.Key_Image));
        if (addImage && imageUri2 != null) {
            StorageReference storageRef = storage1.getReference();
            StorageReference imagesRef = storageRef.child("images/" + imageUri2.getLastPathSegment());
            imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String image = uri.toString();
                Log.d("ttrttrttrtrttrttrtrtrt", "123456789-0---" + image);

                post.put(Constants.Key_Picture, image);

                // Update the post data and save it to the database
                savePostData(post);
            }).addOnFailureListener(exception -> {
                // Handle failure to get download URL
                Log.e("TAG", "Failed to get download URL: " + exception.getMessage());
                showToast("Failed to upload image");
            });
        } else {
            post.put(Constants.Key_Picture, imagePosted);
            Log.d("ttrttrttrtrttrttrtrtrt", "123456789-0--------''''");

            // Update the post data and save it to the database
            savePostData(post);
        }
    }

    private void savePostData(Map<String, Object> post) {
        post.put(Constants.Key_Post_Time, new Date());
        post.put(Constants.Key_P_ID, docName + "post");
        post.put(Constants.Key_Likes, "0");
        post.put(Constants.Key_Comments, "0");
        Log.d("12345566777", "123456789" + managePreferences.getString(Constants.Key_Image));

        // Add a new document with a generated ID
        CollectionReference usersCol = db.collection(Constants.Key_Collection_Posts);

        Log.d("12345566777", "123456789-0---" + postnum);
        Log.d("12345566777", "123456789---000---" + docName);
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
    private void getAllDocumentNames() {
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Posts);

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
                            countThePosts(documentNames);

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void countThePosts(List<String> documentNames){
        docName=managePreferences.getString(Constants.Key_Email);

        for(int i=0;i < documentNames.size(); i++){
            String namesss = documentNames.get(i);
            if(namesss.contains(managePreferences.getString(Constants.Key_Email))) {
                Log.d("12345566777", "123456789" + namesss);
                postnum ++;
                Log.d("12345566", "===========---------" + postnum);

            }
        }
        docName = docName + "-" + postnum;
        Log.d("12345566", "===========---------" + docName);

    }private void uploadAnImage(Uri uriFile){

        StorageReference storageRef = storage1.getReference();

// Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child( uriFile.getLastPathSegment());

// Create a reference to 'images/mountains.jpg'
        StorageReference imagesRef = storageRef.child("images/" + uriFile.getLastPathSegment());
        //ImageView imageView = (ImageView)findViewById(android.R.id.text1);



        UploadTask uploadTask  = imagesRef.putFile(uriFile);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

            }
        });

    }


    private void showToast(String mssg){
        Toast.makeText(this, mssg, Toast.LENGTH_SHORT).show();
    }


}
/*
// Step 1: Select an image from the device
Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
intent.setType("image/*");
startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

// Step 2 and 3: Handle the selected image URI and upload to Firebase Storage
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
        Uri imageUri = data.getData();

        // Create a storage reference
        StorageReference storageRef = storage.getReference();
        // Create a reference to the image file in Firebase Storage
        StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

        // Upload the file to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Step 4: Save the download URL in Firebase Realtime Database
                        saveImageUrlToDatabase(imageUrl);
                    });
                })
                .addOnFailureListener(exception -> {
                    // Image upload failed
                    Log.e("TAG", "Image upload failed: " + exception.getMessage());
                    // Handle the error
                });
    }
}

// Step 4: Save the download URL in Firebase Realtime Database
private void saveImageUrlToDatabase(String imageUrl) {
    // Get a reference to the Firebase Realtime Database
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

    // Save the image URL under a specific node in the database
    databaseRef.child("profileImageUrl").setValue(imageUrl)
            .addOnSuccessListener(aVoid -> {
                // Image URL saved successfully in the database
                Log.d("TAG", "Image URL saved in database");
            })
            .addOnFailureListener(e -> {
                // Error occurred while saving the image URL
                Log.e("TAG", "Error saving image URL: " + e.getMessage());
            });
}
 */