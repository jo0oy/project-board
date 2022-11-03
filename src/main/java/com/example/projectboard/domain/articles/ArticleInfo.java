package com.example.projectboard.domain.articles;

import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.domain.articles.articlehashtags.ArticleHashtag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArticleInfo {
    @Getter
    @ToString
    @AllArgsConstructor
    @Builder
    public static class MainInfo {
        private Long articleId;
        private Long userId;
        private String title;
        private String content;
        private Set<HashtagInfo> hashtagInfos;
        private String createdBy;
        private LocalDateTime createdAt;

        public MainInfo(Article entity) {
            this.articleId = entity.getId();
            this.userId = entity.getUserId();
            this.title = entity.getTitle();
            this.content = entity.getContent();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();
            this.hashtagInfos = entity.getHashtags().stream()
                    .map(HashtagInfo::new)
                    .collect(Collectors.toUnmodifiableSet());
        }

        public MainInfo(Article entity, Set<ArticleHashtag> hashtags) {
            this.articleId = entity.getId();
            this.userId = entity.getUserId();
            this.title = entity.getTitle();
            this.content = entity.getContent();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();
            this.hashtagInfos = hashtags.stream()
                    .map(HashtagInfo::new)
                    .collect(Collectors.toUnmodifiableSet());
        }

        public String getHashtagStringContent() {
            var list = this.hashtagInfos.stream()
                    .map(HashtagInfo::getActualHashtagName)
                    .collect(Collectors.toList());
            return String.join(",", list);
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @Builder
    public static class ArticleWithCommentsInfo {
        private Long articleId;
        private Long userId;
        private String title;
        private String content;
        private List<HashtagInfo> hashtagInfos;
        private String createdBy;
        private LocalDateTime createdAt;
        private List<ArticleCommentInfo.SimpleInfo> comments;
        private boolean likedArticle;
        public ArticleWithCommentsInfo(Article entity,
                                       List<ArticleCommentInfo.SimpleInfo> comments,
                                       boolean likedArticle) {

            this.articleId = entity.getId();
            this.userId = entity.getUserId();
            this.title = entity.getTitle();
            this.content = entity.getContent();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();

            this.hashtagInfos = entity.getHashtags().stream()
                    .map(HashtagInfo::new)
                    .collect(Collectors.toUnmodifiableList());

            this.comments = new ArrayList<>();
            if (comments != null && comments.size() > 0) {
                this.comments.addAll(comments);
            }

            this.likedArticle = likedArticle;
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @Builder
    public static class HashtagInfo {
        private Long hashtagId;
        private String actualHashtagName;
        private String hashtagName;

        public HashtagInfo(ArticleHashtag entity) {
            var hashtagEntity = entity.getHashtag();
            this.hashtagId = hashtagEntity.getId();
            this.actualHashtagName = entity.getActualHashtagName();
            this.hashtagName = hashtagEntity.getHashtagName();
        }
    }
}
