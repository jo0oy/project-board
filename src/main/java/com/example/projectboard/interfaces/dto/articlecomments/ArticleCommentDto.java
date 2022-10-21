package com.example.projectboard.interfaces.dto.articlecomments;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


public class ArticleCommentDto {

    @Getter
    @ToString
    @Builder
    public static class RegisterReq {
        @NotNull
        private Long articleId;

        @NotBlank @Size(min = 5, max = 500)
        private String commentBody;
    }

    @Getter
    @ToString
    @Builder
    public static class UpdateReq {

        @NotBlank @Size(min = 5, max = 500)
        private String commentBody;
    }

    @Getter
    @ToString
    @Builder
    public static class RegisterForm {
        @NotNull
        private Long parentArticleId;

        @NotBlank @Size(min = 5, max = 500)
        private String commentBody;
    }

    @Getter
    @ToString
    @Builder
    public static class UpdateForm {
        @NotNull
        private Long parentArticleId;

        @NotBlank @Size(min = 5, max = 500)
        private String updateCommentBody;
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
        private Long userId;
        private String commentBody;
        private String createdBy;
        private LocalDateTime createdAt;

    }

    @ToString
    @Getter
    @Builder
    public static class SimpleInfoResponse {
        private Long commentId;
        private Long userId;
        private String commentBody;
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
