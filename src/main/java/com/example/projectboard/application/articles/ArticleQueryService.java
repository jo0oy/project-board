package com.example.projectboard.application.articles;

import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleQueryService {

    ArticleInfo.MainInfo getArticle(Long articleId);

    ArticleInfo.ArticleWithCommentsInfo getArticleWithComments(Long articleId, String principalUsername);

    Page<ArticleInfo.MainInfo> articles(ArticleCommand.SearchCondition condition, Pageable pageable);

    List<ArticleInfo.MainInfo> articleList(ArticleCommand.SearchCondition condition);

    Page<ArticleInfo.MainInfo> articlesByHashtagId(Long hashtagId, Pageable pageable);

    Page<ArticleInfo.MainInfo> articlesByHashtagName(String hashtagName, Pageable pageable);

    long articleCount();
}
