package com.example.projectboard.application.likes;

import com.example.projectboard.domain.articles.ArticleInfo;
import com.example.projectboard.domain.likes.ArticleLikeCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleLikeQueryService {

    Page<ArticleInfo.MainInfo> articlesLiked(String principalUsername, ArticleLikeCommand.SearchCondition condition, Pageable pageable);
}
