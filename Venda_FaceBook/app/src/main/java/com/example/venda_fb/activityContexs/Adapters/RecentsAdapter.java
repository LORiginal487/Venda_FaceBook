package com.example.venda_fb.activityContexs.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Listeners.RecentConvoClickerListener;
import com.example.venda_fb.activityContexs.utilities.ChatMessage;
import com.example.venda_fb.activityContexs.utilities.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.conversationViewHolder>{
    private final List<ChatMessage> chatMessages;
    private final RecentConvoClickerListener recentConvoClickerListener;

    public RecentsAdapter(List<ChatMessage> chatMessages, RecentConvoClickerListener recentConvoClickerListener) {
        this.chatMessages = chatMessages;
        this.recentConvoClickerListener = recentConvoClickerListener;
    }

    @NonNull
    @Override
    public conversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chats, parent, false);
        return new conversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull conversationViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }


    class conversationViewHolder extends RecyclerView.ViewHolder {
            View itemView1;
            public conversationViewHolder(@NonNull View itemView) {

                super(itemView);
                itemView1 = itemView;
            }
            void setData(ChatMessage chatMessage){
                TextView name = itemView1.findViewById(R.id.theirName);
                name.setText(chatMessage.convoName);
                TextView time = itemView1.findViewById(R.id.time);
                time.setText(getRedableDate(chatMessage.dateObject));
                TextView text = itemView1.findViewById(R.id.lastText);
                text.setText(chatMessage.message);
                RoundedImageView pp = itemView1.findViewById(R.id.image);
                pp.setImageBitmap(getUserImage(chatMessage.convoImage));

                name.getRootView().setOnClickListener(v -> {
                    User user = new User();
                    user.id = chatMessage.convoID;
                    user.email = chatMessage.convoID;
                    user.name = chatMessage.convoName;
                    user.image = chatMessage.convoImage;
                    recentConvoClickerListener.onConvoClicked(user);
                });
            }
            private Bitmap getUserImage(String encodedImg) {
                byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
            private String getRedableDate(Date date){
                return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
            }
        }
}

//private List<String> lastText;
//    private List<String> lastTextTime;
//    List<Inbox.person2> personList;
//
//
//public RecentsAdapter(List<Inbox.person2> personList){
//    this.personList = personList;
//}
//    @NonNull
//    @Override
//    public RecentsAdapter.RecentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chats, parent, false);
//        return new RecentsViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecentsAdapter.RecentsViewHolder holder, int position) {
//        holder.setPersonData(personList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return personList.size();
//    }
//
//    public class RecentsViewHolder extends RecyclerView.ViewHolder {
//        View itemView;
//
//        public RecentsViewHolder(@NonNull View itemView) {
//            super(itemView);
//            this.itemView = itemView;
//        }
//
//        void setPersonData(Inbox.person2 person2c) {
//
//
//            TextView names = itemView.findViewById(R.id.theirName);
//            names.setText((person2c.name2).toUpperCase());
//
//            TextView lastTextV = itemView.findViewById(R.id.lastText);
//            lastTextV.setText(person2c.message2);
//
//            TextView textTimeV = itemView.findViewById(R.id.time);
//            textTimeV.setText(person2c.time2);
//
////            RoundedImageView imageProfile = itemView.findViewById(R.id.image);
////            imageProfile.setImageBitmap(getUserImage(user.image));
//
//            Log.d("RRERRERRERRERERRERRERRERERRERRERRRRERRE","--=====    5.1");
//
//        }
//
//        private Bitmap getUserImage(String encodedImg) {
//            byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
//            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        }
