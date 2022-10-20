package com.example.projectboard.domain.hashtags;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByHashtagNameIgnoreCase(String hashtagName);

    List<Hashtag> findByHashtagNameIsContainingIgnoreCase(String hashtagName);

    List<Hashtag> findByHashtagNameIn(Set<String> hashtagNames);
}
