package com.example.projectboard.domain.articlecomments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

public class ArticleCommentInfo {

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class MainInfo {
        private Long commentId;
        private Long articleId;
        private Long userId;
        private String content;
        private String createdBy;
        private LocalDateTime createdAt;

        public MainInfo() {
        }

        public MainInfo(ArticleComment entity, Long articleId) {
            this.commentId = entity.getId();
            this.articleId = articleId;
            this.userId = entity.getUserId();
            this.content = entity.getContent();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();
        }
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class SimpleInfo {
        private Long commentId;
        private Long userId;
        private String content;
        private String createdBy;
        private LocalDateTime createdAt;

        public SimpleInfo() {
        }

        public SimpleInfo(ArticleComment entity) {
            this.commentId = entity.getId();
            this.userId = entity.getUserId();
            this.content = entity.getContent();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();
        }
    }
}
