package com.example.projectboard.domain.likes;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

public class ArticleLikeCommand {

    @Getter
    @ToString
    @Builder
    public static class SearchCondition {
        private String title;
        private LocalDateTime createdAt;
        private String createdBy;

        public ArticleLikeSearchCondition toSearchCondition() {
            return ArticleLikeSearchCondition.builder()
                    .title(title)
                    .createdBy(createdBy)
                    .createdAt((createdAt == null) ? null : createdAt.toLocalDate().atStartOfDay())
                    .build();
        }
    }
}
