package com.example.projectboard.domain.articles;

import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.domain.articles.articlehashtags.ArticleHashtag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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
        private Integer viewCount;
        private Set<HashtagInfo> hashtagInfos;
        private String createdBy;
        private LocalDateTime createdAt;

        public MainInfo(Article entity) {
            this.articleId = entity.getId();
            this.userId = entity.getUserId();
            this.title = entity.getTitle();
            this.content = entity.getContent();
            this.viewCount = entity.getViewCount();
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
            this.viewCount = entity.getViewCount();
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
        private Integer viewCount;
        private List<HashtagInfo> hashtagInfos;
        private String createdBy;
        private LocalDateTime createdAt;
        private Set<ArticleCommentInfo.WithChildCommentsInfo> comments;
        private boolean likedArticle;
        public ArticleWithCommentsInfo(Article entity,
                                       Set<ArticleCommentInfo.WithChildCommentsInfo> comments,
                                       boolean likedArticle) {

            this.articleId = entity.getId();
            this.userId = entity.getUserId();
            this.title = entity.getTitle();
            this.content = entity.getContent();
            this.viewCount = entity.getViewCount();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();

            this.hashtagInfos = entity.getHashtags().stream()
                    .map(HashtagInfo::new)
                    .collect(Collectors.toUnmodifiableList());

            this.comments = organizeChildComments(comments); // 대댓글 구조로 재정렬

            this.likedArticle = likedArticle;
        }

        private static Set<ArticleCommentInfo.WithChildCommentsInfo> organizeChildComments(Set<ArticleCommentInfo.WithChildCommentsInfo> infos) {
            var map = infos.stream()
                    .collect(Collectors.toMap(ArticleCommentInfo.WithChildCommentsInfo::getCommentId, Function.identity()));

            map.values().stream()
                    .filter(ArticleCommentInfo.WithChildCommentsInfo::hasParent)
                    .forEach(comment -> {
                        var parentComment = map.get(comment.getParentId());
                        parentComment.getChildComments().add(comment);
                    });

            return map.values().stream()
                    .filter(comment -> !comment.hasParent())
                    .collect(Collectors.toCollection(() ->
                            new TreeSet<>(Comparator
                                    .comparing(ArticleCommentInfo.WithChildCommentsInfo::getCreatedAt)
                                    .reversed()
                                    .thenComparingLong(ArticleCommentInfo.WithChildCommentsInfo::getCommentId)
                            )
                    ));
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
