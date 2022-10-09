package com.example.projectboard.domain.articles;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ArticleRepositoryCustom {

    Optional<Article> findByIdFetchJoinUserAccount(Long articleId);

    List<Article> findAll(ArticleSearchCondition condition);

    Page<Article> findAll(ArticleSearchCondition condition, Pageable pageable);
}
