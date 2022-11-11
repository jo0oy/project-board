package com.example.projectboard.domain.articles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends
        JpaRepository<Article, Long>, ArticleRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Article a WHERE a.userId = :userId")
    void bulkDeleteByUserAccount_Id(@Param("userId") Long userId);

    @Modifying
    @Query("update Article a set a.viewCount = a.viewCount + 1 where a.id = :articleId")
    void updateViewCount(@Param("articleId") Long articleId);
}

