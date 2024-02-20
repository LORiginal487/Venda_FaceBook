package com.example.venda_fb.activityContexs.Listeners;


import com.example.venda_fb.activityContexs.utilities.User;

public interface UserListener {
    void onUserClick(User user);
    void OnAddFriendClick(User user);
    void OnConfirmFriendClick(User user);
}
