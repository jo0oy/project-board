package com.example.projectboard.domain.articles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class ArticleInfo {
    private Long articleId;
    private String title;
    private String content;
    private String hashtag;

    private String createdBy;

    private LocalDateTime createdAt;

    public ArticleInfo(Article entity) {
        this.articleId = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.hashtag = (entity.getHashtag() != null) ? entity.getHashtag() : null;
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
    }
}
