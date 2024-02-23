package com.example.venda_fb.activityContexs.Listeners;

import com.example.venda_fb.activityContexs.utilities.Post;
import com.example.venda_fb.activityContexs.utilities.User;

public interface LikesAndCommentListener {
    void onCommentClicked(Post post);
    void onLikeClicked(Post post);
    void onPictureClick(Post post);
    void onPersonClicked(User user);
    void onThreeDotsClick(String type, String postId);
}
