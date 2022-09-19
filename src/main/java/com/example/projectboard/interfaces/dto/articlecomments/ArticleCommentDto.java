package com.example.projectboard.interfaces.dto.articlecomments;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


public class ArticleCommentDto {

    @Getter
    @ToString
    @Builder
    public static class RegisterReq {
        private Long articleId;
        private String content;
    }

    @Getter
    @ToString
    @Builder
    public static class UpdateReq {
        private String content;
    }

    @Getter
    @ToString
    @Builder
    public static class SearchCondition {
        private LocalDateTime createdAt;
        private String createdBy;

        public static SearchCondition of(String createdAt,
                                         String createdBy) {
            LocalDateTime date = null;
            if (Objects.nonNull(createdAt)) {
                date = LocalDate.parse(createdAt).atStartOfDay();
            }

            return ArticleCommentDto.SearchCondition.builder()
                    .createdAt(date)
                    .createdBy(createdBy)
                    .build();
        }
    }

    @ToString
    @Getter
    @Builder
    public static class MainInfoResponse {
        private Long commentId;
        private Long articleId;
        private String content;
        private String createdBy;
        private LocalDateTime createdAt;
    }

    @ToString
    @Getter
    @Builder
    public static class SimpleInfoResponse {
        private Long commentId;
        private String content;
        private String createdBy;
        private LocalDateTime createdAt;
    }

    @ToString
    @Getter
    @Builder
    public static class GroupByArticleInfoResponse {
        private Long articleId;
        private List<MainInfoResponse> comments;
    }
}
