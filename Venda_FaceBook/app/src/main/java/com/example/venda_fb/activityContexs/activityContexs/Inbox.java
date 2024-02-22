package com.example.venda_fb.activityContexs.activityContexs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Adapters.RecentsAdapter;
import com.example.venda_fb.activityContexs.Listeners.RecentConvoClickerListener;
import com.example.venda_fb.activityContexs.utilities.ChatMessage;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.example.venda_fb.activityContexs.utilities.User;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inbox extends AppCompatActivity implements RecentConvoClickerListener {
    User userTo;
    private List<ChatMessage> convos= new ArrayList<>();
    RecentsAdapter recentsAdapter;
    EditText searchbar;
    User userDt;
    ManagePreferences managePreferences;
    ProgressBar loadingProgressBar;
    RecyclerView recentsView;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        //___________-----
        userTo =(User) getIntent().getSerializableExtra(Constants.Key_User);
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());
        callViews();
        ListenMessages();
        displayRecents();
        whileSearching();
    }
    private void callViews(){

        loadingProgressBar = findViewById(R.id.progressBar);
        recentsView = findViewById(R.id.recycleVrecents);
        searchbar = findViewById(R.id.searchText);


    }private void whileSearching(){
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchbar.getText().toString().isEmpty()){
                    displayRecents();
                }else {
                    Log.d("rteyruihbcfdid","567890--00000001");
                    Filtering(searchbar.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(searchbar.getText().toString().isEmpty()){
                    displayRecents();
                }else {
                    Log.d("rteyruihbcfdid","567890--00000002");
                    Filtering(searchbar.getText().toString());

                }
            }
        });
    }
    private void Filtering(String text) {


        List<ChatMessage> filteredConvo = new ArrayList<>();

        for (ChatMessage chatMessage : convos) {
            if (chatMessage.convoID.toUpperCase().contains(text.toUpperCase().trim()) ||
                    chatMessage.convoName.toUpperCase().contains(text.toUpperCase().trim()) ||
                    chatMessage.message.toUpperCase().contains(text.toUpperCase().trim()))  {
                Log.d("rteyruihbcfdid","567890--0000000"+chatMessage.convoID);
                filteredConvo.add(chatMessage);
                Log.d("rteyruihbcfdid","567890--0000000"+filteredConvo.size());
            }

        }
        Log.d("rteyruihbcfdid","567890--0000000"+filteredConvo.size());
        callAdapter(filteredConvo);

    }
    private void displayRecents(){

        callAdapter(convos);

    }
    private void callAdapter(List<ChatMessage> convos1){
        recentsAdapter = new RecentsAdapter(convos1, this);
        recentsView.setAdapter(recentsAdapter);
    }

    public void goHome(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }



    private void ListenMessages(){
        db.collection(Constants.Key_Collection_Convos)
                .whereEqualTo(Constants.Key_Sender_ID, managePreferences.getString(Constants.Key_Users_Id))
                .addSnapshotListener(eventListener);
        db.collection(Constants.Key_Collection_Convos)
                .whereEqualTo(Constants.Key_Receiver_ID, managePreferences.getString(Constants.Key_Users_Id))
                .addSnapshotListener(eventListener);
    }
    private EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null){
            return;
        }
        if(value != null)
        {
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderId = documentChange.getDocument().getString(Constants.Key_Sender_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.Key_Receiver_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = senderId;
                    chatMessage.receiverID = receiverId;
                    if(managePreferences.getString(Constants.Key_Users_Id).equals(senderId)){
                        chatMessage.convoImage = documentChange.getDocument().getString(Constants.Key_Receiver_Image);
                        chatMessage.convoName = documentChange.getDocument().getString(Constants.Key_Receiver_Name);
                        chatMessage.convoID = documentChange.getDocument().getString(Constants.Key_Receiver_ID);
                    }else{
                        chatMessage.convoImage = documentChange.getDocument().getString(Constants.Key_Sender_Image);
                        chatMessage.convoName = documentChange.getDocument().getString(Constants.Key_Sender_Name);
                        chatMessage.convoID = documentChange.getDocument().getString(Constants.Key_Sender_ID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.Key_Last_Message);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.Key_Timestamp);

                    convos.add(chatMessage);
                }else if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for(int i = 0; i <convos.size(); i++){
                        String senderId = documentChange.getDocument().getString(Constants.Key_Sender_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.Key_Receiver_ID);
                        if(convos.get(i).senderID.equals(senderId ) && convos.get(i).receiverID.equals(receiverId)){
                            convos.get(i).message = documentChange.getDocument().getString(Constants.Key_Last_Message);
                            convos.get(i).dateObject = documentChange.getDocument().getDate(Constants.Key_Timestamp);
                            break;
                        }
                    }
                }
            }
            Collections.sort(convos, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            recentsAdapter.notifyDataSetChanged();
            recentsView.smoothScrollToPosition(0);
            recentsView.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.GONE);
        }

    };

        @Override
        public void onConvoClicked(User user) {
            Intent intent = new Intent(getApplicationContext(), ChatScreen.class);
            intent.putExtra(Constants.Key_User, user);
            startActivity(intent);
        }
    }
//    private void getChats() {
//
//        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Chat);
//        collectionRef.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//
//
//                            for (DocumentSnapshot document : task.getResult()) {
//                                // Get the document ID (name) and add it to the list
//                                String documentName = document.getId();
//                                documentNames.add(documentName);
//                                Log.d("RRERRERRERRERERRERRERRERERRERRERRRRERRE","--=====    0.111");
//
//                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                                    Log.d("RRERRERRERRERERRERRERRERERRERRERRRRERRE","--=====    0.22221");
//
//                                        User user = new User();
//                                        String receiverEmail =queryDocumentSnapshot.getString(Constants.Key_Receiver_ID);
//                                        if(receiverEmail == managePreferences.getString(Constants.Key_Email)){
//                                            Log.d("RRERRERRERRERERRERRERRERERRERRERRRRERRE","--=====    1");
//                                            getUserById(queryDocumentSnapshot.getString(Constants.Key_Sender_ID),queryDocumentSnapshot.getString(Constants.Key_Message), queryDocumentSnapshot.getString(Constants.Key_Timestamp));
//                                        }
//                                        else{
//                                            String senderEmail =queryDocumentSnapshot.getString(Constants.Key_Sender_ID);
//                                            if(senderEmail == managePreferences.getString(Constants.Key_Email)){
//                                                Log.d("RRERRERRERRERERRERRERRERERRERRERRRRERRE","--=====    2");
//                                                getUserById(queryDocumentSnapshot.getString(Constants.Key_Receiver_ID),queryDocumentSnapshot.getString(Constants.Key_Message), queryDocumentSnapshot.getString(Constants.Key_Timestamp));
//                                            }
//                                        }
//
//
//                                }
//
//
//                            }
//
//
//                            // Now you have a list of all document names
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//    }

//    private void getUserById( String email,String lastText, String lastTextTime) {
//        Log.d("RRERRERRERRERERRERRERRERERRERRERRRRERRE","--=====    3");
//
//        CollectionReference collectionRef = db.collection(Constants.Key_Collection_Users);
//        collectionRef.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//
//                            for (DocumentSnapshot document : task.getResult()) {
//                                // Get the document ID (name) and add it to the list
//                                String documentName = document.getId();
//                                documentNames.add(documentName);
//
//                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                                    Log.d("RRERRERRERRERERRERRERRERERRERRERRRRERRE","--=====    4");
//
//                                    if (documentName.equals(queryDocumentSnapshot.getId()) &&documentName.contains(email)) {
//                                        User user = new User();
//                                        user.name = queryDocumentSnapshot.getString(Constants.Key_Name);
//                                        user.surname = queryDocumentSnapshot.getString(Constants.Key_Surname);
//                                        user.email = queryDocumentSnapshot.getString(Constants.Key_Email);
//                                        user.image = queryDocumentSnapshot.getString(Constants.Key_Image);
//                                        user.token = queryDocumentSnapshot.getString(Constants.Key_FCM_Token);
//                                        user.id = queryDocumentSnapshot.getId();
//                                        user.biot = queryDocumentSnapshot.getString(Constants.Key_Bio);
//                                        user.bckGnd = queryDocumentSnapshot.getString(Constants.Key_BackgroundPic);
//                                        if(!userz.contains(user)){
//                                            userz.add(user);
//                                            texts.add(lastText);
//                                            timeOfTexts.add(lastTextTime);
//                                            Log.d("RRERRERRERRERERRERRERRERERRERRERRRRERRE","--=====   5");
//
//                                        }
//
//
//                                    }
//
//                                }
//                                if (userz.size() > 0) {
//                                    RecentsAdapter recentsAdapter = new RecentsAdapter(userz, texts, timeOfTexts, Inbox.this);
//                                    recentsView.setAdapter(recentsAdapter);
//                                    recentsView.setVisibility(View.VISIBLE);
//                                    Log.d("RRERRERRERRERERRERRERRERERRERRERRRRERRE","--=====    6");
//
//                                    loadingProgressBar.setVisibility(View.GONE);
//
//                                }
//
//                            }
//
//
//                            // Now you have a list of all document names
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//    }
//
//
//
//    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
//        // Check for errors
//        if (error != null) {
//            return; // Exit if there is an error
//        }
//
//        // Check if the value is not null
//        if (value != null) {
//            int count = person2list.size(); // Get the current count of chat messages
//
//            // Iterate through document changes in the QuerySnapshot
//            for (DocumentChange documentChange : value.getDocumentChanges()) {
//                if (documentChange.getType() == DocumentChange.Type.ADDED) {
//                    // If a new document is added, create a ChatMessage object and add it to the list
//                    person2class = new person2();
//                    person2class.message2 = documentChange.getDocument().getString(Constants.Key_Message);
//                        person2class.time2 = getRedableDate(documentChange.getDocument().getDate(Constants.Key_Timestamp));
//                       person2class.name2 = documentChange.getDocument().getString(Constants.Key_Sender_ID);
//                        person2class.dateObject = documentChange.getDocument().getDate(Constants.Key_Timestamp);
//                        if (!person2list.contains(person2class.name2)){
//                            person2list.add(person2class);
//                        }
//
//
////                    String receiverEmail = documentChange.getDocument().getString(Constants.Key_Receiver_ID);
////                    String senderEmail = documentChange.getDocument().getString(Constants.Key_Sender_ID);
////                    if(receiverEmail == managePreferences.getString(Constants.Key_Email)){
////                        person2class.message2 = documentChange.getDocument().getString(Constants.Key_Message);
////                        person2class.time2 = getRedableDate(documentChange.getDocument().getDate(Constants.Key_Timestamp));
////                        person2class.name2 = documentChange.getDocument().getString(Constants.Key_Sender_ID);
////                        person2class.dateObject = documentChange.getDocument().getDate(Constants.Key_Timestamp);
////                        if (!person2list.contains(person2class.name2)){
////                            person2list.add(person2class);
////                        }
////                    }
////                    else if(senderEmail == managePreferences.getString(Constants.Key_Email)){
////                        person2class.message2 = documentChange.getDocument().getString(Constants.Key_Message);
////                        person2class.time2 = getRedableDate(documentChange.getDocument().getDate(Constants.Key_Timestamp));
////                        person2class.name2 = documentChange.getDocument().getString(Constants.Key_Sender_ID);
////                        person2class.dateObject = documentChange.getDocument().getDate(Constants.Key_Timestamp);
////                        if (!person2list.contains(person2class.name2)){
////                            person2list.add(person2class);
////                        }
////                    }
//
//
//                }
//            }
//
//            // Sort chatMessages based on dateObject
//            Collections.sort(person2list, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
//
//            recentsView.setVisibility(View.VISIBLE); // Make the RecyclerView visible
//        }
//
//        // Log statements for debugging
//        System.out.println("ok we in 1");
//        Log.d("ok", "22222222222222222");
//        loadingProgressBar.setVisibility(View.GONE); // Hide the progress bar
//    };
//    private void ListenMessages(){
//        db.collection(Constants.Key_Collection_Chat)
//                .whereEqualTo(Constants.Key_Sender_ID, managePreferences.getString(Constants.Key_Email))
//                .whereEqualTo(Constants.Key_Receiver_ID, userTo.email)
//                .addSnapshotListener(eventListener);
//        db.collection(Constants.Key_Collection_Chat)
//                .whereEqualTo(Constants.Key_Sender_ID, userTo.email)
//                .whereEqualTo(Constants.Key_Receiver_ID, managePreferences.getString(Constants.Key_Email))
//                .addSnapshotListener(eventListener);
//    }
//    private String getRedableDate(Date date){
//        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
//    }
//    public class person2{
//        public String name2, image2, message2, time2;
//        public Date dateObject;
//    }
