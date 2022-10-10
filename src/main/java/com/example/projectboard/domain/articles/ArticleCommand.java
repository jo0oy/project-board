package com.example.projectboard.domain.articles;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

public class ArticleCommand {

    @Getter
    @ToString
    @Builder
    public static class RegisterReq {
        private String title;
        private String content;
        private String hashtag;

        public Article toEntity(Long userId) {
            return Article.ArticleWithHashtag()
                    .title(title)
                    .content(content)
                    .hashtag(hashtag)
                    .userId(userId)
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    public static class UpdateReq {
        private String title;
        private String content;
        private String hashtag;
    }

    @Getter
    @ToString
    @Builder
    public static class SearchCondition {
        private String title;
        private String hashtag;
        private LocalDateTime createdAt;
        private String createdBy;

        public ArticleSearchCondition toSearchCondition() {
            return ArticleSearchCondition.builder()
                    .title(title)
                    .hashtag(hashtag)
                    .createdBy(createdBy)
                    .createdAt((createdAt == null) ? null : createdAt.toLocalDate().atStartOfDay())
                    .build();
        }
    }
}
