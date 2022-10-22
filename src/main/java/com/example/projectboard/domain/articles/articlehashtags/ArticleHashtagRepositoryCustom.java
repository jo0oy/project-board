package com.example.projectboard.domain.articles.articlehashtags;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleHashtagRepositoryCustom {

    Page<ArticleHashtag> findByHashtagId(Long hashtagId, Pageable pageable);

    Page<ArticleHashtag> findByHashtagNameIgnoreCase(String hashtagName, Pageable pageable);

    List<ArticleHashtag> findByHashtagNameIgnoreCase(String hashtagName);
}
