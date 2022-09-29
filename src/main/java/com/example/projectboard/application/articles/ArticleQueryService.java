package com.example.projectboard.application.articles;

import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleQueryService {

    ArticleInfo getArticle(Long articleId);
    Page<ArticleInfo> articles(ArticleCommand.SearchCondition condition, Pageable pageable);
    List<ArticleInfo> articleList(ArticleCommand.SearchCondition condition);
}
