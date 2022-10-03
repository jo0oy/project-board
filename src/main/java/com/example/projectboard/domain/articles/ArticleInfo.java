package com.example.projectboard.domain.articles;

import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArticleInfo {
    @Getter
    @ToString
    @AllArgsConstructor
    @Builder
    public static class MainInfo {
        private Long articleId;
        private String title;
        private String content;
        private String hashtag;
        private String createdBy;
        private LocalDateTime createdAt;

        public MainInfo(Article entity) {
            this.articleId = entity.getId();
            this.title = entity.getTitle();
            this.content = entity.getContent();
            this.hashtag = (entity.getHashtag() != null) ? entity.getHashtag() : null;
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();
        }

    }

    @Getter
    @ToString
    @AllArgsConstructor
    @Builder
    public static class ArticleWithCommentsInfo {
        private Long articleId;
        private String title;
        private String content;
        private String hashtag;
        private String createdBy;
        private LocalDateTime createdAt;
        private List<ArticleCommentInfo.SimpleInfo> comments;
        public ArticleWithCommentsInfo(Article entity,
                                       List<ArticleCommentInfo.SimpleInfo> comments) {

            this.articleId = entity.getId();
            this.title = entity.getTitle();
            this.content = entity.getContent();
            this.hashtag = (entity.getHashtag() != null) ? entity.getHashtag() : null;
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();
            this.comments = new ArrayList<>();
            if (comments != null && comments.size() > 0) {
                this.comments.addAll(comments);
            }
        }
    }

}
