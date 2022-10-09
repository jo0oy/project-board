package com.example.projectboard.application.articles;

import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;

public interface ArticleCommandService {

    ArticleInfo.MainInfo registerArticle(String username, ArticleCommand.RegisterReq command);

    void update(Long articleId, String username, ArticleCommand.UpdateReq command);

    void delete(Long articleId, String username);
}
