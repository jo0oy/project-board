package com.example.projectboard.domain.articles;

import lombok.Getter;

@Getter
public enum SearchType {
    TITLE("제목"),
    CREATED_BY("작성자"),
    HASHTAG("해시태그"),
    CREATED_AT("작성일");

    private final String description;

    SearchType(String description) {
        this.description = description;
    }
}
