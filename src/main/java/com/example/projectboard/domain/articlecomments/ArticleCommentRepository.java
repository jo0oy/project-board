package com.example.projectboard.domain.articlecomments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleCommentRepository extends
        JpaRepository<ArticleComment, Long>, ArticleCommentRepositoryCustom{

    @Query("select ac from ArticleComment ac where ac.article.id = :articleId")
    List<ArticleComment> findByArticleId(@Param("articleId") Long articleId);
}
