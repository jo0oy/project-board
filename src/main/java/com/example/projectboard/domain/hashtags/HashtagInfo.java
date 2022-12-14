package com.example.projectboard.domain.hashtags;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@Builder
public class HashtagInfo {
    private Long hashtagId;
    private String hashtagName;
    private Integer totalCount;

    public HashtagInfo(Hashtag entity) {
        this.hashtagId = entity.getId();
        this.hashtagName = entity.getHashtagName();
        this.totalCount = entity.getArticles().size();
    }
}
