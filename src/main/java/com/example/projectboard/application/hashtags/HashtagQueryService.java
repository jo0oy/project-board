package com.example.projectboard.application.hashtags;

import com.example.projectboard.domain.hashtags.Hashtag;

import java.util.Set;

public interface HashtagQueryService {

    Set<Hashtag> hashtagListByHashtagNames(Set<String> hashtagNames);
}
