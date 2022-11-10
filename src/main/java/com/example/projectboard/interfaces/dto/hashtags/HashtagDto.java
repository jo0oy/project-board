package com.example.projectboard.interfaces.dto.hashtags;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class HashtagDto {

    @ToString
    @Getter
    @Builder
    public static class MainInfoResponse {
        private Long hashtagId;
        private String hashtagName;
        private Integer totalCount;
    }
}
