package com.example.projectboard.domain.likes;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class ArticleLikeSearchCondition {
    private String title;
    private LocalDateTime createdAt;
    private String createdBy;
}
