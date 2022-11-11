package com.example.projectboard.application.articles;

import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ArticleCommandService {

    ArticleInfo.MainInfo registerArticle(String principalUsername, ArticleCommand.RegisterReq command);

    ArticleInfo.MainInfo registerArticleWithValidHashtags(String principalUsername, ArticleCommand.RegisterReq command);

    void viewCountUp(Long articleId, HttpServletRequest request, HttpServletResponse response);

    void update(Long articleId, String principalUsername, ArticleCommand.UpdateReq command);

    void delete(Long articleId, String principalUsername);
}
