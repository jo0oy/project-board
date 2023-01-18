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
    @Query("DELETE FROM ArticleComment ac WHERE ac.id = :id")
    void deleteById(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ArticleComment ac WHERE ac.article.id = :articleId")
    void bulkDeleteByArticleId(@Param("articleId") Long articleId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ArticleComment ac WHERE ac.userId = :userId")
    void bulkDeleteByUserAccountId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ArticleComment ac WHERE ac.parent.id = :parentId")
    void bulkDeleteByParentId(@Param("parentId") Long parentId);
}
