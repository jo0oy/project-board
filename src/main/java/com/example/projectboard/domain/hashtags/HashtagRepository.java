package com.example.projectboard.domain.hashtags;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByHashtagNameIgnoreCase(String hashtagName);

    Page<Hashtag> findByHashtagNameContainingIgnoreCase(String hashtagName, Pageable pageable);

    List<Hashtag> findByHashtagNameIn(Set<String> hashtagNames);
}
