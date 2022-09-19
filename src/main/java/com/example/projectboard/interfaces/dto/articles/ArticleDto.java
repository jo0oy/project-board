package com.example.projectboard.interfaces.dto.articles;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class ArticleDto {

    @Getter
    @ToString
    @Builder
    public static class RegisterReq {
        private String title;
        private String content;
        private String hashtag;
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

        public static SearchCondition of(String title,
                                         String hashtag,
                                         String createdAt,
                                         String createdBy) {

            LocalDateTime date = null;

            if (Objects.nonNull(createdAt)) {
                date = LocalDate.parse(createdAt).atStartOfDay();
            }

            return ArticleDto.SearchCondition.builder()
                    .title(title)
                    .hashtag(hashtag)
                    .createdAt(date)
                    .createdBy(createdBy)
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    public static class MainInfoResponse {
        private Long articleId;
        private String title;
        private String content;
        private String hashtag;
        private String createdBy;
        private LocalDateTime createdAt;
    }
}
