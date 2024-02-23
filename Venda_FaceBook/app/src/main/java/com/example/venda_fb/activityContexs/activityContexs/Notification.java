package com.example.venda_fb.activityContexs.activityContexs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Adapters.NotificationsAdapter;
import com.example.venda_fb.activityContexs.Adapters.RecentsAdapter;
import com.example.venda_fb.activityContexs.Listeners.NotificationListener;
import com.example.venda_fb.activityContexs.utilities.ChatMessage;
import com.example.venda_fb.activityContexs.utilities.Constants;
import com.example.venda_fb.activityContexs.utilities.ManagePreferences;
import com.example.venda_fb.activityContexs.utilities.Notification_c;
import com.example.venda_fb.activityContexs.utilities.Post;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Notification extends AppCompatActivity implements NotificationListener {
    FirebaseFirestore db;
    ManagePreferences managePreferences;
    List<Notification_c> notis;
    RecyclerView notiView;
    NotificationsAdapter notificationsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //***(--------------
        db = FirebaseFirestore.getInstance();
        managePreferences = new ManagePreferences(getApplicationContext());
        callViews();
        displayNotifcations();
        ListenNotification();
    }

    private void displayNotifcations() {
        notis = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(notis, Notification.this);
        notiView.setAdapter(notificationsAdapter);
    }

    private void callViews() {
        notiView = findViewById(R.id.recycleVposts);

    }


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
    private void ListenNotification(){
        db.collection(Constants.Key_Collection_Notifications)
                .whereEqualTo(Constants.Key_Noti_Email, managePreferences.getString(Constants.Key_Email))
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
                Log.d("qwedrfrgucuyjdfgas", "325444444q6546344");
                    String name = documentChange.getDocument().getString(Constants.Key_Noti_Names);
                    String pp = documentChange.getDocument().getString(Constants.Key_Noti_pp);
                    String text = documentChange.getDocument().getString(Constants.Key_Noti_Text);
                    String time = getRedableDate(documentChange.getDocument().getDate(Constants.Key_Noti_Time));
                    String email = documentChange.getDocument().getString(Constants.Key_Noti_Email);
                    Notification_c notification = new Notification_c();
                    notification.names = name;
                    notification.imagePP = pp;
                    notification.notificationTXT = text;
                    notification.notiTime = time;
                    notification.email = email;
                    notification.dateObject = documentChange.getDocument().getDate(Constants.Key_Noti_Time);

                    notis.add(notification);
                    Log.d("qwedrfrgucuyjdfgas", "325444444q6546344"+notis.size());


                }

            }
            Collections.sort(notis, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            notificationsAdapter.notifyDataSetChanged();
            notiView.smoothScrollToPosition(0);
            Log.d("qwedrfrgucuyjdfgas", "325444444q6546344------------");

            notiView.setVisibility(View.VISIBLE);

        }

    };
    private String getRedableDate(Date date){
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }

    @Override
    public void onNotiClicked(Post post) {
        Intent intent = new Intent(getApplicationContext(), CommentsLayout.class);
        // Pass any necessary data to the CommentsLayout activity using extras
        intent.putExtra(Constants.Key_Post, post);
        startActivity(intent);
    }
}