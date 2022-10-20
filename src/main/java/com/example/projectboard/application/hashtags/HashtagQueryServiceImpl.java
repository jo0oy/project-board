package com.example.projectboard.application.hashtags;

import com.example.projectboard.domain.hashtags.Hashtag;
import com.example.projectboard.domain.hashtags.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class HashtagQueryServiceImpl implements HashtagQueryService {

    private final HashtagRepository hashtagRepository;

    @Override
    public Set<Hashtag> hashtagListByHashtagNames(Set<String> hashtagNames) {
        return new HashSet<>(
                hashtagRepository.findByHashtagNameIn(
                        hashtagNames.stream().map(String::toLowerCase)
                                .collect(Collectors.toUnmodifiableSet()))
        );
    }
}
