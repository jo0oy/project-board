package com.example.projectboard.domain.articlecomments;

import com.example.projectboard.domain.articles.Article;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

public class ArticleCommentCommand {

    @Getter
    @ToString
    @Builder
    public static class RegisterReq {

        private Long articleId;
        private String commentBody;

        public ArticleComment toEntity(Article article, Long userId) {
            return ArticleComment.builder()
                    .article(article)
                    .commentBody(commentBody)
                    .userId(userId)
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    public static class UpdateReq {
        private String commentBody;
    }

    @Getter
    @ToString
    @Builder
    public static class SearchCondition {

        private LocalDateTime createdAt;
        private String createdBy;

        public ArticleCommentSearchCondition toSearchCondition() {
            return ArticleCommentSearchCondition.builder()
                    .createdAt((createdAt == null) ? null : createdAt.toLocalDate().atStartOfDay())
                    .createdBy(createdBy)
                    .build();
        }
    }
}
