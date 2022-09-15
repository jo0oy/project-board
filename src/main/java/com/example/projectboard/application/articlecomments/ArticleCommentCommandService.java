package com.example.projectboard.application.articlecomments;

import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;

public interface ArticleCommentCommandService {
    ArticleCommentInfo.MainInfo registerComment(ArticleCommentCommand.RegisterReq command);

    void update(Long commentId, ArticleCommentCommand.UpdateReq command);
}
