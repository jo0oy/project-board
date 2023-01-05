package com.example.projectboard.domain.articlecomments;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ArticleCommentInfo {

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MainInfo {
        private Long commentId;
        private Long articleId;
        private Long userId;
        private Long parentId;
        private String commentBody;
        private String createdBy;
        private LocalDateTime createdAt;

        public MainInfo(ArticleComment entity, Long articleId) {
            this.commentId = entity.getId();
            this.articleId = articleId;
            this.userId = entity.getUserId();
            this.parentId = entity.getParent().getId();
            this.commentBody = entity.getCommentBody();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();
        }

        public MainInfo(ArticleComment entity, Long articleId, Long parentId) {
            this.commentId = entity.getId();
            this.articleId = articleId;
            this.userId = entity.getUserId();
            this.parentId = parentId;
            this.commentBody = entity.getCommentBody();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();
        }
    }

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleInfo {
        private Long commentId;
        private Long userId;
        private Long parentId;
        private String commentBody;
        private String createdBy;
        private LocalDateTime createdAt;

        public SimpleInfo(ArticleComment entity) {
            this.commentId = entity.getId();
            this.userId = entity.getUserId();
            this.commentBody = entity.getCommentBody();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();
            this.parentId = Objects.nonNull(entity.getParent()) ? entity.getParent().getId() : null;
        }

        public boolean hasParent() {
            return Objects.nonNull(this.parentId);
        }
    }

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WithChildCommentsInfo {
        private Long commentId;
        private Long articleId;
        private Long userId;
        private Long parentId;
        private String commentBody;
        private String createdBy;
        private LocalDateTime createdAt;
        private Set<WithChildCommentsInfo> childComments;

        public WithChildCommentsInfo(ArticleComment entity) {
            this.commentId = entity.getId();
            this.articleId = entity.getArticle().getId();
            this.userId = entity.getUserId();
            this.commentBody = entity.getCommentBody();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();

            // 대댓글 정렬 기준(Comparator) 설정
            // 1) 'createdAt' 오름차순,  2) 'commentId' 오름차순
            var childComparator = Comparator
                    .comparing(WithChildCommentsInfo::getCreatedAt)
                    .thenComparingLong(WithChildCommentsInfo::getCommentId);

            this.childComments = new TreeSet<>(childComparator);
            this.childComments.addAll(entity.getChilds().stream()
                    .map(WithChildCommentsInfo::new).collect(Collectors.toSet()));

            this.parentId = Objects.nonNull(entity.getParent()) ? entity.getParent().getId() : null;
        }

        public boolean hasParent() {
            return Objects.nonNull(this.getParentId());
        }
    }
}
