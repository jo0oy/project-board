package com.example.projectboard.domain.articles;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class ArticleSearchCondition {

    private String title;

    private String hashtag;

    private LocalDateTime createdAt;

    private String createdBy;
}
