package com.example.projectboard.domain.articlecomments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleCommentRepository extends
        JpaRepository<ArticleComment, Long>, ArticleCommentRepositoryCustom{

    @Query("SELECT ac FROM ArticleComment ac WHERE ac.article.id = :articleId")
    List<ArticleComment> findByArticleId(@Param("articleId") Long articleId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ArticleComment ac WHERE ac.article.id = :articleId")
    void deleteByArticleId(@Param("articleId") Long articleId);
}
