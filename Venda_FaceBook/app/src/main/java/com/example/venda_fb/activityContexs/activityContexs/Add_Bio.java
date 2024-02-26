package com.example.venda_fb.activityContexs.activityContexs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Add_Bio extends AppCompatActivity {
    EditText inBio;
    AppCompatImageView buttonDn;
    FirebaseFirestore db;
    ManagePreferences managePreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bio);
        callViews();
        Ontyping();
    }

    private void callViews(){
        inBio = findViewById(R.id.inNewBio);
        buttonDn = findViewById(R.id.addbiob);
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());
    }
    private void Ontyping(){
        inBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inBio.getText().toString().isEmpty()){
                    buttonDn.setVisibility(View.GONE);
                }else{
                    buttonDn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(inBio.getText().toString().isEmpty()){
                    buttonDn.setVisibility(View.GONE);
                }else{
                    buttonDn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void goHome(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


    public void addBioNow(View view) {
        if(inBio.getText().toString().isEmpty()){
            showToast("Enter new Bio");
        }else{
            addInDb();
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            startActivity(intent);
        }
    }
    private void showToast(String mssg){
        Toast.makeText(this, mssg, Toast.LENGTH_SHORT).show();
    }
    private void addInDb(){
        showToast("Checking");

        // Reference to the Firebase database
        // Create a new user with a first and last name
        Map<String, Object> newBio = new HashMap<>();
        newBio.put(Constants.Key_Bio, inBio.getText().toString());
        managePreferences.putString(Constants.Key_Bio, inBio.getText().toString());
        Log.d("12345566777", "123456789"+managePreferences.getString(Constants.Key_Image));

        // Add a new document with a generated ID
        CollectionReference usersCol = db.collection(Constants.Key_Collection_Users);

        usersCol.document(managePreferences.getString(Constants.Key_Email))
                .update(newBio).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast("Bio updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Retry");
                    }
                });
    }

}