package com.example.projectboard.domain.articles;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends
        JpaRepository<Article, Long>, ArticleRepositoryCustom{
}

