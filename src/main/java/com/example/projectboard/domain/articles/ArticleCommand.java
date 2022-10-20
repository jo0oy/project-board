package com.example.projectboard.domain.articles;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleCommand {

    @Getter
    @ToString
    @Builder
    public static class RegisterReq {
        private String title;
        private String content;

        private List<String> hashtagNames;

        public Article toEntity(Long userId) {
            return Article.of(title, content, userId);
        }
    }

    @Getter
    @ToString
    @Builder
    public static class UpdateReq {
        private String title;
        private String content;
        private List<String> hashtagNames;
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
