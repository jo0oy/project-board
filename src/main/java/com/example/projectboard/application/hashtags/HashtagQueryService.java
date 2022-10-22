package com.example.projectboard.application.hashtags;

import com.example.projectboard.domain.hashtags.Hashtag;
import com.example.projectboard.domain.hashtags.HashtagInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface HashtagQueryService {

    Page<HashtagInfo> hashtags(Pageable pageable);

    Set<Hashtag> hashtagListByHashtagNames(Set<String> hashtagNames);
}
