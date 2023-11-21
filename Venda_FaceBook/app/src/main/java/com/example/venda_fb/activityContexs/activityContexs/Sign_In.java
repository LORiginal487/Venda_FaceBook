package com.example.venda_fb.activityContexs.activityContexs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.example.venda_fb.activityContexs.utilities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Sign_In extends AppCompatActivity {
    EditText emailInBox, passwordInBox;
    String email, password, name, surname,image;
    Button signIn_btn;
    ProgressBar loadingProgressBar;
    FirebaseFirestore db;
    DocumentReference docRef;
    User userDt;
    ManagePreferences managePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //-----------------
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());

        callViews();
    }

    private void callViews() {
        emailInBox = findViewById(R.id.emailIn);
        passwordInBox = findViewById(R.id.passwordIn);
        signIn_btn = findViewById(R.id.buttonSignIn);
        loadingProgressBar = findViewById(R.id.progressBar);
    }

    public void singIn(View view) {

        validate();
    }

    public void singUp(View view) {
        Intent intent = new Intent(getApplicationContext(), Sign_Up.class);
        startActivity(intent);
    }
    private void validate(){
        loading(true);
        email = emailInBox.getText().toString();
        password = passwordInBox.getText().toString();
        docRef = db.collection(Constants.Key_Collection_Users).document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String passwordDB = document.getString(Constants.Key_Password);
                        if (password.equals(passwordDB)){
                            name = document.getString(Constants.Key_Name);
                            surname = document.getString(Constants.Key_Surname);
                            image = document.getString(Constants.Key_Image);
                            managePreferences.putBoolean(Constants.Key_Is_Signed_In, true);
                            managePreferences.putString(Constants.Key_Users_Id, docRef.getId());
                            managePreferences.putString(Constants.Key_Name, name);
                            managePreferences.putString(Constants.Key_Email, email);
                            managePreferences.putString(Constants.Key_Image, image);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            showToast("invalid password");
                            loading(false);
                        }


                    } else{
                        showToast("email is invalid");
                        loading(false);
                    }
                }
            }
        });
    }
    private void loading(boolean isLoading){
        Log.d("l3 1111111", "_______________");
        if(isLoading){
            signIn_btn.setVisibility(View.INVISIBLE);
            loadingProgressBar.setVisibility(View.VISIBLE);
            Log.d("l4 1111111", "_______________");
        }else{
            signIn_btn.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.INVISIBLE);
            Log.d("l5 1111111", "_______________");
        }

    }


    private void showToast(String mssg){
        Toast.makeText(this, mssg, Toast.LENGTH_SHORT).show();
    }

}