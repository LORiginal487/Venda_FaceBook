package com.example.venda_fb.activityContexs.activityContexs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.example.venda_fb.activityContexs.utilities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

public class Profile extends AppCompatActivity {
    RoundedImageView imageViewPP;
    TextView nameV, bioV;
    User userDt;
    ManagePreferences managePreferences;
    DocumentReference docRef;
    FirebaseFirestore db;

    String fullName, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        managePreferences = new ManagePreferences(getApplicationContext());

        //---------------------
        callViews();
        db = FirebaseFirestore.getInstance();
        setEverything();

    }
    private void setEverything(){
        getFromDB();
        nameV.setText(fullName);

        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageViewPP.setImageBitmap(bitmap);
    }
    private void callViews(){
        userDt = new User();
        imageViewPP = findViewById(R.id.myPP);
        nameV = findViewById(R.id.nameDis);
        bioV = findViewById(R.id.Bio);
    }
    private void getFromDB(){
        docRef = db.collection(Constants.Key_Collection_Users).document(managePreferences.getString(Constants.Key_Email));
        Log.d("1234565432345676", "eretyuioiuytfrdfhjhgc");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("1234565432345676", "_______------___");
                    if (document.exists()) {
                        Log.d("1234565432345676", "--------------------");
                        String name = document.getString(Constants.Key_Name);
                        Log.d("1234565432345676", ""+name);
                        String surname = document.getString(Constants.Key_Surname);
                        Log.d("1234565432345676", ""+surname);

                        assert name != null;
                        assert surname != null;
                        fullName= name.toUpperCase()+" "+surname.toUpperCase();
                        Log.d("1234565432345676", ""+fullName);

                        image = document.getString(Constants.Key_Image);

                    }
                }else {
                    Log.d("1234565432345676", ""+task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("1234565432345676", ""+e);
            }
        });
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
}