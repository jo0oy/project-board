package com.example.projectboard.interfaces.dto.articles;

import com.example.projectboard.domain.articles.SearchType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

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
        private String title;
        private String content;
        private String hashtag;
    }

    @Getter
    @Setter
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
        private String hashtag;
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
        private String hashtag;
        private String createdBy;
        private LocalDateTime createdAt;
        private List<CommentInfoResponse> comments;
    }

    @Getter
    @ToString
    @Builder
    public static class CommentInfoResponse {
        private Long commentId;
        private Long userId;
        private String content;
        private String createdBy;
        private LocalDateTime createdAt;
    }
}
