package com.example.projectboard.application.likes;

public interface LikeCommandService {

    void pushLike(Long articleId, String username);
}
