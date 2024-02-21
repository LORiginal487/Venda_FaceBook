package com.example.venda_fb.activityContexs.activityContexs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Adapters.PeopleAdapter;
import com.example.venda_fb.activityContexs.Listeners.UserListener;
import com.example.venda_fb.activityContexs.utilities.ConstantMethods;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Friends extends AppCompatActivity implements UserListener {
    List<String> documentNames = new ArrayList<>();
    List<User> allUsers = new ArrayList<>();
    List<User> friendsList = new ArrayList<>();
    List<User> notFriends = new ArrayList<>();
    FirebaseFirestore db;
    ManagePreferences managePreferences;
    RecyclerView recycleV;
    ProgressBar loadingProgressBar;
    TextView requests, suggestions, friends;
    List<User> userz = new ArrayList<>();
    List<String> listEmails1= new ArrayList<>();
    Map<String, Object> friendsMap1 = new HashMap<>();
    User user1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        callViews();

        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());
        showFriends();
    }
    public void addPostPage(View view) {
        Intent intent = new Intent(getApplicationContext(), AddaPost.class);
        startActivity(intent);
    }
    private void callViews(){
        loadingProgressBar = findViewById(R.id.progressBar);
        recycleV = findViewById(R.id.recycleVposts);
        requests = findViewById(R.id.Friend_Request);
        suggestions = findViewById(R.id.suggestionsBtn);
        friends = findViewById(R.id.friendsBtn2);
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

    public void showSuggests(View view) {
        loading(true);
        requests.setVisibility(View.VISIBLE);
        suggestions.setVisibility(View.GONE);
        friends.setVisibility(View.VISIBLE);

        ConstantMethods.returnAllUsers(new ConstantMethods.UserListRetrievedListener() {
            @Override
            public void onUserListRetrieved(List<User> users) {
                // Do something with the list of users
                allUsers = users;
                checkFriends();
                findAllNonFrnds();
                Log.d("lmmkmmkmkmkmkmkmkmkmkmkmmmkm", "_______________"+allUsers.size());
                Log.d("lmmkmmkmkmkmkmkmkmkmkmkmmmkm", "_______________"+friendsList.size());
                Log.d("lmmkmmkmkmkmkmkmkmkmkmkmmmkm", "_______________"+notFriends.size());
                PeopleAdapter peopleAdapter = new PeopleAdapter(notFriends,Constants.Key_Suggestion, Friends.this);
                recycleV.setAdapter(peopleAdapter);
                recycleV.setVisibility(View.VISIBLE);
                loading(false);
            }
        });




    }
    private void findAllNonFrnds(){
        for (User user : allUsers) {
            boolean isFriend = false;

            // Check if the user is in friendsList
            for (User friend : friendsList) {
                if (user.email.equals(friend.email)) {
                    isFriend = true;
                    break;
                }
            }

            // If the user is not in friendsList, add them to notFriends list
            if (!isFriend) {
                notFriends.add(user);
            }
        }
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

    public void showRequests(View view) {
        requests.setVisibility(View.GONE);
        suggestions.setVisibility(View.VISIBLE);
        friends.setVisibility(View.VISIBLE);
        ConstantMethods.FindUsersFrindsR(managePreferences.getString(Constants.Key_Email),Friends.this,recycleV);
        loading(false);
    }

    public void openFriends(View view) {
        showFriends();
    }
    private void showFriends() {
        requests.setVisibility(View.VISIBLE);
        suggestions.setVisibility(View.VISIBLE);
        friends.setVisibility(View.GONE);
        ConstantMethods.FindFriends(managePreferences.getString(Constants.Key_Email),Friends.this,recycleV);
        loading(false);
    }
    private void checkFriends(){

        ConstantMethods.OnFriendsMapReadyListener listener = new ConstantMethods.OnFriendsMapReadyListener() {
            @Override
            public void onFriendsMapReady(Map<String, Object> friendsMap) {
                friendsMap1 = friendsMap;

                Set<String> keysFM = friendsMap1.keySet();
                for(String key : keysFM){
                    if(!key.equals(Constants.Key_My_Email)) {
                        ConstantMethods.getUserByEmail(key, new ConstantMethods.OnUserRetrievedListener() {
                            @Override
                            public void onUserRetrieved(User user) {
                                if (user != null) {
                                    friendsList.add(user);

                                } else {
                                    showToast("User not found for key: " + key);
                                }
                            }
                        });
                    }
                }
            }
        };

        // Call the method with the listener
        ConstantMethods.getAllFriendsMap(managePreferences.getString(Constants.Key_Email), listener);

    }



    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this, User_Profile.class);
        // Pass any necessary data to the CommentsLayout activity using extras
        intent.putExtra(Constants.Key_User, user);
        startActivity(intent);
    }

    @Override
    public void OnAddFriendClick(User user) {
        ConstantMethods.CheckIfFrnds(managePreferences.getString(Constants.Key_Email), user.email);
        sendNotification(Constants.Key_Added_Noti, user.email);


    }

    @Override
    public void OnConfirmFriendClick(User user) {
        ConstantMethods.ConfirmingAfrnd(managePreferences.getString(Constants.Key_Email), user.email);
        ConstantMethods.FindUsersFrindsR(managePreferences.getString(Constants.Key_Email),Friends.this,recycleV);
        sendNotification(Constants.Key_Confirm_Not, user.email);
    }
    private void sendNotification(String type, String email){
        Map<String, Object> noti = new HashMap<>();
        noti.put(Constants.Key_Noti_Names, managePreferences.getString(Constants.Key_Name));
        noti.put(Constants.Key_Noti_pp, managePreferences.getString(Constants.Key_Image));
        noti.put(Constants.Key_Noti_Text, type);
        noti.put(Constants.Key_Noti_Time, new Date());
        noti.put(Constants.Key_Noti_Email, email);
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Notifications);
        collectionRef.add(noti).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                showToast("Done!");


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

    private void checkFrndStatus(User user){

    }
    private void showToast(String mssg){
        Toast.makeText(this, mssg, Toast.LENGTH_SHORT).show();
    }
    private List<User> getRequests(){
        userz = null;


        return userz;
    }

}
