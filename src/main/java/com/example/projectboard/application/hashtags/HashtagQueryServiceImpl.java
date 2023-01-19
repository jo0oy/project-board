package com.example.projectboard.application.hashtags;

import com.example.projectboard.common.util.PageRequestUtils;
import com.example.projectboard.domain.hashtags.Hashtag;
import com.example.projectboard.domain.hashtags.HashtagInfo;
import com.example.projectboard.domain.hashtags.HashtagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class HashtagQueryServiceImpl implements HashtagQueryService {

    private final HashtagRepository hashtagRepository;

    // 해시태그 리스트 페이징 조회
    @Override
    public Page<HashtagInfo> hashtags(Pageable pageable) {
        return hashtagRepository.findAll(PageRequestUtils.of(pageable)).map(HashtagInfo::new);
    }

    // 해시태그 리스트 페이징 조회 by 해시태그 이름
    @Override
    public Page<HashtagInfo> hashtags(String hashtagName, Pageable pageable) {
        if (!StringUtils.hasText(hashtagName)) {
            return hashtagRepository.findAll(PageRequestUtils.of(pageable)).map(HashtagInfo::new);
        }
        return hashtagRepository.findByHashtagNameContainingIgnoreCase(hashtagName, PageRequestUtils.of(pageable))
                .map(HashtagInfo::new);
    }

    // hashtagName in 쿼리 조회
    @Override
    public Set<Hashtag> hashtagListByHashtagNames(Set<String> hashtagNames) {
        return new HashSet<>(
                hashtagRepository.findByHashtagNameIn(
                        hashtagNames.stream().map(String::toLowerCase)
                                .collect(Collectors.toUnmodifiableSet()))
        );
    }
}
