package com.example.projectboard.domain.articlecomments;

import com.example.projectboard.domain.articles.Article;
import com.example.projectboard.domain.users.UserAccount;
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
        private String content;

        public ArticleComment toEntity(Article article, Long userId) {
            return ArticleComment.builder()
                    .article(article)
                    .content(content)
                    .userId(userId)
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    public static class UpdateReq {
        private Long articleId;
        private String content;
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
