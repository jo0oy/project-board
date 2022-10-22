package com.example.projectboard.application.hashtags;

import com.example.projectboard.domain.hashtags.Hashtag;
import com.example.projectboard.domain.hashtags.HashtagInfo;
import com.example.projectboard.domain.hashtags.HashtagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class HashtagQueryServiceImpl implements HashtagQueryService {

    private final HashtagRepository hashtagRepository;

    @Override
    public Page<HashtagInfo> hashtags(Pageable pageable) {
        return hashtagRepository.findAll(getPageRequest(pageable)).map(HashtagInfo::new);
    }

    @Override
    public Set<Hashtag> hashtagListByHashtagNames(Set<String> hashtagNames) {
        return new HashSet<>(
                hashtagRepository.findByHashtagNameIn(
                        hashtagNames.stream().map(String::toLowerCase)
                                .collect(Collectors.toUnmodifiableSet()))
        );
    }

    private PageRequest getPageRequest(Pageable pageable) {

        if(pageable.getPageNumber() < 0) {
            log.error("IllegalArgumentException. Invalid PageNumber!!");
            throw new IllegalArgumentException("페이지 번호는 0보다 작을 수 없습니다. 올바른 페이지 번호를 입력하세요.");
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    }
}
