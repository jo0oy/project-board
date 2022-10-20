package com.example.projectboard.domain.articles.articlehashtags;

import com.example.projectboard.domain.articles.articlehashtags.ArticleHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleHashtagRepository extends JpaRepository<ArticleHashtag, Long> {

    List<ArticleHashtag> findByArticle_Id(Long articleId);

    List<ArticleHashtag> findByHashtag_Id(Long hashtagId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ArticleHashtag ah WHERE ah.article.id = :articleId")
    void bulkDeleteByArticle_Id(@Param("articleId") Long articleId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ArticleHashtag ah WHERE ah.hashtag.id = :hashtagId")
    void bulkDeleteByHashtag_Id(@Param("hashtagId") Long hashtagId);
}
