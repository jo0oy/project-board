package com.example.projectboard.domain.likes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeRepositoryCustom {

    Page<Like> articleLikesByUsername(String username, ArticleLikeSearchCondition condition, Pageable pageable);
}
