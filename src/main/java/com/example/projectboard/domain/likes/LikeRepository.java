package com.example.projectboard.domain.likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {

    Optional<Like> findByArticle_IdAndUserAccount_Id(Long articleId, Long userId);

    Optional<Like> findByArticle_IdAndUserAccount_Username(Long articleId, String username);

    boolean existsByArticle_IdAndUserAccount_Username(Long articleId, String username);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Like l WHERE l.article.id = :articleId")
    void deleteByArticleId(@Param("articleId") Long articleId);
}
