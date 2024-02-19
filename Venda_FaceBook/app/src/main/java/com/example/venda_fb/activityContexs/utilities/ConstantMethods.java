package com.example.venda_fb.activityContexs.utilities;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venda_fb.activityContexs.Adapters.PeopleAdapter;
import com.example.venda_fb.activityContexs.Listeners.UserListener;
import com.example.venda_fb.activityContexs.activityContexs.Searching;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstantMethods {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    static List<String> documentNames = new ArrayList<>();

    public static List<User> userz = new ArrayList<>();
    static String peopleTshow, image;
    //static Map<String, Object> data = new HashMap<>();

    static User user;

//    public static User getUserByEmail(String email) {
//        db.collection(Constants.Key_Collection_Users)
//                .whereEqualTo(Constants.Key_Email, email)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            user = document.toObject(User.class);
//                        }
//                    } else {
//                        Log.d("TAG", "Error getting user by email: ", task.getException());
//
//                    }
//                });
//        return user;
//    }
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
                        User user = document.toObject(User.class);
                        listener.onUserRetrieved(user); // Pass the retrieved user to the listener
                    }
                } else {
                    Log.d("TAG", "Error getting user by email: ", task.getException());
                    listener.onUserRetrieved(null); // Notify listener of failure
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
                            User user = document.toObject(User.class);
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