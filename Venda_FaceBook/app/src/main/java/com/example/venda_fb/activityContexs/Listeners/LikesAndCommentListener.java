package com.example.venda_fb.activityContexs.Listeners;

import com.example.venda_fb.activityContexs.utilities.Post;

public interface LikesAndCommentListener {
    void onCommentClicked(Post post);
    void onLikeClicked(Post post);
}
