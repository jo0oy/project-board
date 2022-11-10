package com.example.projectboard.domain.articles;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> findAll(ArticleSearchCondition condition);

    Page<Article> findAll(ArticleSearchCondition condition, Pageable pageable);

    Page<Article> findAllByUserId(Long userId, ArticleSearchCondition condition, Pageable pageable);
}
