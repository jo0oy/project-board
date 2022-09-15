package com.example.projectboard.domain.articlecomments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ArticleCommentRepositoryCustom {

    Optional<ArticleCommentInfo.MainInfo> findWithArticleId(Long id);

    Page<ArticleComment> findByArticleId(Long articleId, Pageable pageable);

    List<ArticleCommentInfo.MainInfo> findAllWithArticleId();

    Page<ArticleCommentInfo.MainInfo> findAllWithArticleId(Pageable pageable);

    List<ArticleCommentInfo.MainInfo> findAllWithArticleId(ArticleCommentSearchCondition searchCondition);

    Page<ArticleCommentInfo.MainInfo> findAllWithArticleId(ArticleCommentSearchCondition searchCondition, Pageable pageable);
}
