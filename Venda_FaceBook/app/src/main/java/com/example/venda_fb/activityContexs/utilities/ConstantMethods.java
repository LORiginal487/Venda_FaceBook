package com.example.venda_fb.activityContexs.utilities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venda_fb.activityContexs.Adapters.PeopleAdapter;
import com.example.venda_fb.activityContexs.Listeners.UserListener;
import com.example.venda_fb.activityContexs.activityContexs.Friends;
import com.example.venda_fb.activityContexs.activityContexs.Searching;
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
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ConstantMethods {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    static List<String> documentNames = new ArrayList<>();
    static List<String> emails3 = new ArrayList<>();
    static User user2 = new User();
    static User user = new User();

    public static List<User> userz, userz2 = new ArrayList<>();
    static String peopleTshow, image;
    //static Map<String, Object> data = new HashMap<>();

public interface OnUserRetrievedListener {
    void onUserRetrieved(User user);
}
public static void getUserByEmail(String email, OnUserRetrievedListener listener) {
    db.collection(Constants.Key_Collection_Users)
            .whereEqualTo(Constants.Key_Email, email)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = new User();
                        user.name = document.getString(Constants.Key_Name);
                        user.surname = document.getString(Constants.Key_Surname);
                        user.email =document.getString(Constants.Key_Email);
                        user.image =document.getString(Constants.Key_Image);
                        //user.token =document.getString(Constants.KT);
                        user.id =document.getString(Constants.Key_Users_Id);
                        user.biot =document.getString(Constants.Key_Bio);
                        user.bckGnd = document.getString(Constants.Key_BackgroundPic);
                        listener.onUserRetrieved(user); // Pass the retrieved user to the listener
                    }
                } else {
                    Log.d("TAG", "Error getting user by email: ", task.getException());
                    listener.onUserRetrieved(null); // Notify listener of failure
                }
            });
}
    private static String getRedableDate(Date date){
        return new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()).format(date);
    }
    public interface OnPostRetrievedListener {
        void onPostRetrieved(Post post);
    }
    public static void getPostByID(String postID, OnPostRetrievedListener listener) {
        db.collection(Constants.Key_Collection_Posts)
                .whereEqualTo(Constants.Key_Email, postID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = new Post();
                            post.posterNames = document.getString(Constants.Key_P_Names);

                            post.postTime = getRedableDate(document.getDate(Constants.Key_Post_Time));

                            post.postTxt = document.getString(Constants.Key_P_Text);
                            post.posterPP = document.getString(Constants.Key_Image_pp);
                            post.postedPic = document.getString(Constants.Key_Picture);
                            post.postLikes = document.getString(Constants.Key_Likes);
                            post.postComments = document.getString(Constants.Key_Comments);
                            post.postID = document.getString(Constants.Key_P_ID);

                            listener.onPostRetrieved(post); // Pass the retrieved user to the listener
                        }
                    } else {
                        Log.d("TAG", "Error getting user by email: ", task.getException());
                        listener.onPostRetrieved(null); // Notify listener of failure
                    }
                });
    }



    public static void getAllUsers(RecyclerView peopleView, UserListener userListener) {
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Users);
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
                                    if (documentName.equals(queryDocumentSnapshot.getId())) {
                                        User user = queryDocumentSnapshot.toObject(User.class);
                                        userz.add(user);
                                    }

                                }


                            }if (userz.size() > 0) {

                                PeopleAdapter peopleAdapter = new PeopleAdapter(userz,Constants.Key_Every_Person, userListener);
                                peopleView.setAdapter(peopleAdapter);
                                peopleView.setVisibility(View.VISIBLE);

                            }


                            // Now you have a list of all document names
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }public static void returnAllUsers(UserListRetrievedListener listener) {
        List<User> users = new ArrayList<>();
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Users);
        collectionRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = new User();
                            user.name = document.getString(Constants.Key_Name);
                            user.surname = document.getString(Constants.Key_Surname);
                            user.email =document.getString(Constants.Key_Email);
                            user.image =document.getString(Constants.Key_Image);
                            //user.token =document.getString(Constants.KT);
                            user.id =document.getString(Constants.Key_Users_Id);
                            user.biot =document.getString(Constants.Key_Bio);
                            user.bckGnd = document.getString(Constants.Key_BackgroundPic);

                            users.add(user);
                        }
                        // Notify the listener with the retrieved list of users
                        listener.onUserListRetrieved(users);
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                        // Notify the listener of failure with an empty list
                        listener.onUserListRetrieved(Collections.emptyList());
                    }
                });
    }

    // Define a callback interface to handle the retrieved list of users
    public interface UserListRetrievedListener {
        void onUserListRetrieved(List<User> users);
    }

public static void getAllFriendsMap(String userEmail, final OnFriendsMapReadyListener listener) {
    CollectionReference collectionRef = db.collection(Constants.Key_Collection_Fiends);
    collectionRef.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> data = new HashMap<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String documentName = document.getId();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                if (documentName.equals(queryDocumentSnapshot.getId())) {
                                    // Process the document data and add it to the map
                                    data = queryDocumentSnapshot.getData();
                                }
                            }
                        }
                        // Call the listener with the retrieved data
                        listener.onFriendsMapReady(data);
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                }
            });
}

    // Define a callback interface to handle the retrieved data
    public interface OnFriendsMapReadyListener {
        void onFriendsMapReady(Map<String, Object> friendsMap);
    }
    public static void MakeNotification(User userFrom){

    }
    private static void retrieveUserByEmail(String email, List<User> userList, UserListener userListener, RecyclerView recyclerView) {
        db.collection(Constants.Key_Collection_Users)
                .whereEqualTo(Constants.Key_Email, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }
                        // Notify listener with the retrieved user list
                        if(userList != null && userListener != null) {
                            notifyUserRetrieved(userList, userListener, recyclerView, Constants.Key_Friends);
                        }
                    } else {
                        Log.d("TAG", "Error getting user by email: ", task.getException());
                    }
                });
    }
    public static void deletePost(String postID){
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Posts);
        String[] docName = postID.split("post");
        collectionRef.document(docName[0]).delete().addOnCompleteListener(task -> {

        });
    }
    private static void notifyUserRetrieved(List<User> userList, UserListener userListener, RecyclerView recyclerView, String ppl2show) {
        PeopleAdapter peopleAdapter = new PeopleAdapter(userList, ppl2show, userListener);
        recyclerView.setAdapter(peopleAdapter);
        recyclerView.setVisibility(View.VISIBLE);
    }
    public static void FindFriends(String email, UserListener userListener, RecyclerView recyclerView) {
        userz2.clear();
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Fiends);
        collectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String documentName = document.getId();
                                if (documentName.equals(email)) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Map<String, Object> data = queryDocumentSnapshot.getData();
                                        List<String> emails = ExtractEmails(data);
                                        for (String email2 : emails) {
                                            if (!email2.equals(Constants.Key_My_Email) && data.get(Constants.Key_My_Email).equals(email)) {
                                                retrieveUserByEmail(email2, userz2, userListener, recyclerView);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void FindRequests(){
        List<String> users1EM = new ArrayList<>();
        List<User> usersP = new ArrayList<>();
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Users);
        collectionRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            users1EM.add(user.email);
                        }
                        // Notify the listener with the retrieved list of users

                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                        // Notify the listener of failure with an empty list

                    }
                });
    }
    public static void FindUsersFrindsR(String userEmail, UserListener userListener, RecyclerView recyclerView ){
        userz2.clear();
        emails3.clear();
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Fiends);
        collectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String documentName = document.getId();

                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Map<String, Object> data = queryDocumentSnapshot.getData();
                                        List<String> emails = ExtractEmails(data);
                                        for (String email2 : emails) {
                                            if (email2.equals(userEmail) && data.get(email2).equals(Constants.Key_Friend_Status)) {
                                                emails3.add((String)data.get(Constants.Key_My_Email));
                                            }
                                            Log.d("FGYSFGYTSDRTYWGHWUUIDF", "Error getting documents: "+emails3);
                                        }
//                                        List<String> emails33 = (List<String>) new LinkedHashSet<>(emails3);
//                                        Log.d("FGYSFGYTSDRTYWGHWUUIDF", "Error getting documents: "+emails33);
//
//                                        // LinkedHashSet preserves insertion order
                                        Set<String> set = new HashSet<>(emails3);

                                        // Create a new ArrayList from the set to preserve the order
                                        List<String> listWithoutDuplicates = new ArrayList<>(set);
                                        FindUserByEmail(listWithoutDuplicates,  userListener,  recyclerView);
                                    }
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public static void ConfirmingAfrnd(String userEmail, String frndEmail){
        Map<String, Object> newstatus = new HashMap<>();
        newstatus.put(userEmail, Constants.Key_Friend_Status2);
        Log.d("FGYSFGYTSDRTYWGHWUUIDF", "frndEmail"+frndEmail);
        CollectionReference usersCol = db.collection(Constants.Key_Collection_Fiends);
        DocumentReference docRef = usersCol.document(frndEmail);
        docRef.set(newstatus, SetOptions.merge()).getClass();
        AddFrien2Db(userEmail, frndEmail, Constants.Key_Friend_Status2);
    }public static void AddFrien2Db(String userEmail, String frndEmail, String statusNow){
        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Fiends);
        Map<String, Object> newFrnd1 = new HashMap<>();
        // Fetch the document
        if(statusNow == Constants.Key_Friend_Status) {
            newFrnd1.put(Constants.Key_My_Email, userEmail);
            newFrnd1.put(frndEmail, Constants.Key_Friend_Status);
        }
        else {
            newFrnd1.put(Constants.Key_My_Email, userEmail);
            newFrnd1.put(frndEmail, Constants.Key_Friend_Status2);
        }

            collectionRef.document(userEmail).set(newFrnd1, SetOptions.merge()).getClass();

    }

    public static void CheckIfFrnds(String userEmail, String frndEmail){

        CollectionReference usersCol = db.collection(Constants.Key_Collection_Fiends);

        DocumentReference docRef = usersCol.document(frndEmail);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    if (document.contains(userEmail)) {
                        ConfirmingAfrnd( userEmail,  frndEmail);
                    } else {
                        AddFrien2Db( userEmail,  frndEmail, Constants.Key_Friend_Status);
                    }
                }else {
                    AddFrien2Db( userEmail,  frndEmail, Constants.Key_Friend_Status);

                }
            }
        });
    }
    private static void showToast(String mssg, Context context){
        Toast.makeText( context , mssg, Toast.LENGTH_SHORT).show();
    }

    private static void FindUserByEmail(List<String> emails, UserListener userListener, RecyclerView recyclerView) {
       List<User> userList = new ArrayList<>();
       for(String email: emails) {
           db.collection(Constants.Key_Collection_Users)
                   .whereEqualTo(Constants.Key_Email, email)
                   .get()
                   .addOnCompleteListener(task -> {
                       if (task.isSuccessful() && task.getResult() != null) {

                           for (QueryDocumentSnapshot document : task.getResult()) {
                               User user = document.toObject(User.class);
                               userList.add(user);
                               Log.d("FGYSFGYTSDRTYWGHWUUIDF", "Error getting documents: "+userList);

                           }
                           // Notify listener with the retrieved user list
                           notifyUserRetrieved(userList, userListener, recyclerView, Constants.Key_Request);
                       } else {
                           Log.d("TAG", "Error getting user by email: ", task.getException());
                       }
                   });
       }

    }

    public static void FindUserByEmail(String email) {


        db.collection(Constants.Key_Collection_Users)
                .whereEqualTo(Constants.Key_Email, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            user2.name = document.getString(Constants.Key_Name);
                            user2.surname = document.getString(Constants.Key_Surname);
                            user2.email = document.getString(Constants.Key_Email);
                            user2.image = document.getString(Constants.Key_Image);

                        }
                    } else {
                        Log.d("TAG", "Error getting user by email: ", task.getException());

                    }
                });

    }

    public static List<String> ExtractEmails(Map<String, Object> data){
        List<String> emails = new ArrayList<>();
        Set<String> emailsSet = data.keySet();
        emails.addAll(emailsSet);
        return emails;
    }



//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    // Access the desired field from the document
//                    data = document.getData();
//                    Log.d("ggggggggggggggggggggggggderggd", "---"+data.size());
//                } else {
//                    Log.d("ggggggggggggggggggggggggderggd", "No such document");
//                }
//            } else {
//                Log.e("ggggggggggggggggggggggggderggd", "Error getting document", task.getException());
//            }
}