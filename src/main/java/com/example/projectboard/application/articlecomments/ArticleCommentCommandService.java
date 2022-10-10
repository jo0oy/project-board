package com.example.projectboard.application.articlecomments;

import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;

public interface ArticleCommentCommandService {
    ArticleCommentInfo.MainInfo registerComment(String principalUsername, ArticleCommentCommand.RegisterReq command);

    void update(Long commentId, String principalUsername, ArticleCommentCommand.UpdateReq command);

    void delete(Long commentId, String principalUsername);
}
