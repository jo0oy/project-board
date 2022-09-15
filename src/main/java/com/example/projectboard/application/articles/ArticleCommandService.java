package com.example.projectboard.application.articles;

import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;

public interface ArticleCommandService {

    ArticleInfo registerArticle(ArticleCommand.RegisterReq command);

    void update(Long articleId, ArticleCommand.UpdateReq command);
}
