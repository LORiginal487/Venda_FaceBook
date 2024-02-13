package com.example.venda_fb.activityContexs.activityContexs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Adapters.ChatAdapter;
import com.example.venda_fb.activityContexs.utilities.ChatMessage;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.example.venda_fb.activityContexs.utilities.Post;
import com.example.venda_fb.activityContexs.utilities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatScreen extends AppCompatActivity {
    private User userTo;
    RoundedImageView imagePP;
    TextView chatName, bioV;
    EditText intextBox;
    ImageView background;
    User userDt;
    ManagePreferences managePreferences;
    ProgressBar loadingProgressBar;
    RecyclerView chatRV;
    DocumentReference docRef;
    FirebaseFirestore db;
    List<String> documentNames = new ArrayList<>();
    private List<ChatMessage> chatMessages;
    ChatAdapter chatAdapter;
    List<Post> postz = new ArrayList<>();
    String fullName, image_pp,imageBG, userEm;
    String convoID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        //_______------
        callViews();
        setEverything();
        callClasses();

        ListenMessages();
    }

    private void setEverything(){
        //getIntent().getStringExtra(Constants.Key_Email)
        userTo =(User) getIntent().getSerializableExtra(Constants.Key_User);
        Log.d("1234567654321","----------------");
        fullName = userTo.name;

        image_pp = userTo.image;

        db = FirebaseFirestore.getInstance();
        Log.d("1234567654321","----------------");

        Log.d("1234567654321","----------------");
        chatName.setText(fullName.toUpperCase());
        Log.d("1234567654321","----------------");
        Log.d("1234567654321", "12"+fullName);

        imagePP.setImageBitmap(getBitmap(image_pp));


    }
    private void callViews(){
        imagePP = findViewById(R.id.imagePP);
        chatName = findViewById(R.id.chatName);
        intextBox = findViewById(R.id.typeBar);
        chatRV = findViewById(R.id.recycleVchat);
        loadingProgressBar = findViewById(R.id.progressBar);
    }
    private Bitmap getBitmap(String imageStr){
        byte[] bytes = Base64.decode(imageStr, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public void go2inbox(View view) {
        Intent intent = new Intent(getApplicationContext(), Inbox.class);

        startActivity(intent);
        finish();
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
    public void sendMessage(View view){
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.Key_Sender_ID, managePreferences.getString(Constants.Key_Email));
        message.put(Constants.Key_Receiver_ID, userTo.email);
        message.put(Constants.Key_Message, intextBox.getText().toString());
        message.put(Constants.Key_Timestamp, new Date());
        db.collection(Constants.Key_Collection_Chat).add(message);
        if(convoID != null){
            updateConvo(intextBox.getText().toString());
        }else{
            HashMap<String, Object> convoHM = new HashMap<>();
            convoHM.put(Constants.Key_Sender_ID, managePreferences.getString(Constants.Key_Users_Id));
            convoHM.put(Constants.Key_Sender_Name, managePreferences.getString(Constants.Key_Name));
            convoHM.put(Constants.Key_Sender_Image, managePreferences.getString(Constants.Key_Image));
            convoHM.put(Constants.Key_Receiver_ID, userTo.email);
            convoHM.put(Constants.Key_Receiver_Name, userTo.name);
            convoHM.put(Constants.Key_Receiver_Image, userTo.image);
            convoHM.put(Constants.Key_Last_Message, intextBox.getText().toString());
            convoHM.put(Constants.Key_Timestamp, new Date());
            addConvo2Db(convoHM);

        }
        intextBox.setText(null);
    }
    private String getRedableDate(Date date){
        return new SimpleDateFormat(" HH:mm ", Locale.getDefault()).format(date);
    }
    private void callClasses(){
        managePreferences = new ManagePreferences(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages,
                getBitmap(image_pp),
                managePreferences.getString(Constants.Key_Users_Id));
        chatRV.setAdapter(chatAdapter);

    }
    private void ListenMessages(){
        db.collection(Constants.Key_Collection_Chat)
                .whereEqualTo(Constants.Key_Sender_ID, managePreferences.getString(Constants.Key_Email))
                .whereEqualTo(Constants.Key_Receiver_ID, userTo.email)
                .addSnapshotListener(eventListener);
        db.collection(Constants.Key_Collection_Chat)
                .whereEqualTo(Constants.Key_Sender_ID, userTo.email)
                .whereEqualTo(Constants.Key_Receiver_ID, managePreferences.getString(Constants.Key_Email))
                .addSnapshotListener(eventListener);
    }

    // Define an event listener for Firestore QuerySnapshot
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        // Check for errors
        if (error != null) {
            return; // Exit if there is an error
        }

        // Check if the value is not null
        if (value != null) {
            int count = chatMessages.size(); // Get the current count of chat messages

            // Iterate through document changes in the QuerySnapshot
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    // If a new document is added, create a ChatMessage object and add it to the list
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = documentChange.getDocument().getString(Constants.Key_Sender_ID);
                    chatMessage.receiverID = documentChange.getDocument().getString(Constants.Key_Receiver_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.Key_Message);
                    chatMessage.dateTime = getRedableDate(documentChange.getDocument().getDate(Constants.Key_Timestamp));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.Key_Timestamp);
                    chatMessages.add(chatMessage);
                }
            }

            // Sort chatMessages based on dateObject
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));

            // Check if it's the initial load or if there are new messages
            if (count == 0) {
                // If it's the initial load, notify the adapter to update the UI
                chatAdapter.notifyDataSetChanged();
            } else {
                // If there are new messages, notify the adapter about the range of inserted items
                chatAdapter.notifyItemRangeInserted(chatMessages.size() - 1, chatMessages.size());
                chatRV.smoothScrollToPosition(chatMessages.size() - 1); // Scroll to the last item
            }

            chatRV.setVisibility(View.VISIBLE); // Make the RecyclerView visible
        }

        // Log statements for debugging
        System.out.println("ok we in 1");
        Log.d("ok", "22222222222222222");
        loading(false); // Hide the progress bar
        if(convoID == null){
            checkForConvos();
        }
    };
    private void addConvo2Db(HashMap<String, Object> convoHM){
        db.collection(Constants.Key_Collection_Convos)
                .add(convoHM)
                .addOnSuccessListener(documentReference -> convoID = documentReference.getId());
    }
    private void updateConvo(String message ){
        DocumentReference documentReference =
                db.collection(Constants.Key_Collection_Convos).document(convoID);
        documentReference.update(
                Constants.Key_Last_Message, message,
                Constants.Key_Timestamp, new Date()
        );
    }
    private void checkForConvos(){
        if(chatMessages.size() != 0){
            checkForConvosRemotly(
                    managePreferences.getString(Constants.Key_Users_Id),
                    userTo.id
            );
            checkForConvosRemotly(
                    userTo.id,
                    managePreferences.getString(Constants.Key_Users_Id)
            );
        }
    }
    private void checkForConvosRemotly(String senderId, String receiverId){
        db.collection(Constants.Key_Collection_Convos)
                .whereEqualTo(Constants.Key_Sender_ID, senderId )
                .whereEqualTo(Constants.Key_Receiver_ID, receiverId)
                .get()
                .addOnCompleteListener(convoCompleteListener);

    }
    private final OnCompleteListener<QuerySnapshot> convoCompleteListener = task ->
    {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            convoID = documentSnapshot.getId();
        }
    };


}