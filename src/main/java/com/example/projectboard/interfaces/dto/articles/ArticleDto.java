package com.example.projectboard.interfaces.dto.articles;

import com.example.projectboard.domain.articles.SearchType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ArticleDto {

    @Getter
    @Setter
    @ToString
    @Builder
    public static class RegisterReq {
        @NotBlank @Size(min = 5, max = 70)
        private String title;

        @NotBlank @Size(min = 10, max = 10000)
        private String content;

        private String hashtagContent;
    }

    @Getter
    @Setter
    @ToString
    @Builder
    public static class UpdateReq {
        @NotBlank @Size(min = 5, max = 70)
        private String title;

        @NotBlank @Size(min = 10, max = 10000)
        private String content;

        private String hashtagContent;
    }

    @Getter
    @Setter
    @ToString
    @Builder
    public static class RegisterForm {
        @NotBlank @Size(min = 5, max = 70)
        private String title;

        @NotBlank @Size(min = 10, max = 10000)
        private String content;

        private String hashtagContent;
    }

    @Getter
    @Setter
    @ToString
    @Builder
    public static class UpdateForm {
        @NotNull
        private Long articleId;

        @NotBlank @Size(min = 5, max = 70)
        private String title;

        @NotBlank @Size(min = 10, max = 10000)
        private String content;

        private String hashtagContent;
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

            if (StringUtils.hasText(createdAt)) {
                date = LocalDate.parse(createdAt).atStartOfDay();
            }

            return ArticleDto.SearchCondition.builder()
                    .title(title)
                    .hashtag(hashtag)
                    .createdAt(date)
                    .createdBy(createdBy)
                    .build();
        }

        public static SearchCondition of(String title,
                                         String createdAt) {
            return of(title, null, createdAt, null);
        }

        public static SearchCondition of(String title,
                                         String createdAt,
                                         String createdBy) {
            return of(title, null, createdAt, createdBy);
        }

        public static SearchCondition of(SearchType searchType, String searchValue) {

            if(Objects.isNull(searchType) || !StringUtils.hasText(searchValue)) return SearchCondition.of(null, null, null, null);

            switch (searchType) {
                case TITLE: return SearchCondition.of(searchValue, null, null, null);
                case HASHTAG: return SearchCondition.of(null, searchValue, null, null);
                case CREATED_AT: return SearchCondition.of(null, null, searchValue, null);
                case CREATED_BY: return SearchCondition.of(null, null, null, searchValue);
            }

            return SearchCondition.of(null, null, null, null);
        }
    }

    @Getter
    @ToString
    @Builder
    public static class MainInfoResponse {
        private Long articleId;
        private Long userId;
        private String title;
        private String content;
        private Integer viewCount;
        private List<HashtagInfoResponse> hashtagInfos;
        private String createdBy;
        private LocalDateTime createdAt;
    }

    @Getter
    @ToString
    @Builder
    public static class ArticleWithCommentsResponse {
        private Long articleId;
        private Long userId;
        private String title;
        private String content;
        private Integer viewCount;
        private List<HashtagInfoResponse> hashtagInfos;
        private String createdBy;
        private LocalDateTime createdAt;
        private List<CommentInfoResponse> comments;
        private boolean likedArticle;
    }

    @Getter
    @ToString
    @Builder
    public static class HashtagInfoResponse {
        private Long hashtagId;
        private String actualHashtagName;
        private String hashtagName;
    }

    @Getter
    @ToString
    @Builder
    public static class CommentInfoResponse {
        private Long commentId;
        private Long userId;
        private String commentBody;
        private String createdBy;
        private LocalDateTime createdAt;
    }
}
