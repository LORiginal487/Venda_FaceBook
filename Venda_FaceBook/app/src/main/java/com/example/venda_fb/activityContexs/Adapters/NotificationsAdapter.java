package com.example.venda_fb.activityContexs.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venda_fb.R;
import com.example.venda_fb.activityContexs.Listeners.NotificationListener;
import com.example.venda_fb.activityContexs.Listeners.RecentConvoClickerListener;
import com.example.venda_fb.activityContexs.utilities.ChatMessage;
import com.example.venda_fb.activityContexs.utilities.ConstantMethods;
import com.example.venda_fb.activityContexs.utilities.Notification_c;
import com.example.venda_fb.activityContexs.utilities.Post;
import com.example.venda_fb.activityContexs.utilities.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.notificationsViewHolder>{
    private final List<Notification_c> notis;

    NotificationListener notificationListener;

    public NotificationsAdapter(List<Notification_c> notis, NotificationListener notificationListener) {
        this.notis = notis;
        notificationListener = this.notificationListener;
    }

    @NonNull
    @Override
    public NotificationsAdapter.notificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent, false);
        return new NotificationsAdapter.notificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.notificationsViewHolder holder, int position) {
        holder.setData(notis.get(position));
    }

    @Override
    public int getItemCount() {
        return notis.size();
    }


    class notificationsViewHolder extends RecyclerView.ViewHolder {
        View itemView1;
        public notificationsViewHolder(@NonNull View itemView) {

            super(itemView);
            itemView1 = itemView;
        }
        void setData(Notification_c noti){
            TextView name = itemView1.findViewById(R.id.theirName);
            name.setText(noti.names);
            TextView time = itemView1.findViewById(R.id.time);
            time.setText(noti.notiTime);
            TextView text = itemView1.findViewById(R.id.lastText);
            text.setText(noti.notificationTXT);
            RoundedImageView pp = itemView1.findViewById(R.id.image);
            pp.setImageBitmap(getUserImage(noti.imagePP));
            Log.d("qwedrfrgucuyjdfgas", "325444444q6546344"+noti.names);
            name.getRootView().setOnClickListener(v -> {
                ConstantMethods.getPostByID(noti.postID, new ConstantMethods.OnPostRetrievedListener() {
                    @Override
                    public void onPostRetrieved(Post post) {
                        notificationListener.onNotiClicked(post);
                    }
                });

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

