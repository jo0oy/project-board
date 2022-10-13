package com.example.projectboard.domain.articlecomments;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class ArticleCommentSearchCondition {
    private LocalDateTime createdAt;
    private String createdBy;
}
