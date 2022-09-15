package com.example.projectboard.application.articles;

import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleQueryServiceTest {

    @Autowired
    private ArticleQueryService articleQueryService;

    @Autowired
    private ArticleRepository articleRepository;

    @DisplayName("[성공] 게시글 단건 조회 테스트")
    @Test
    void givenArticleId_whenGetArticle_thenWorksFine() {
        // given
        var articleId = 2L;

        // when
        var result = articleQueryService.getArticle(articleId);

        var findArticle = articleRepository.findById(articleId).orElse(null);

        // then
        assertThat(result).isNotNull();
        assertThat(findArticle).isNotNull();
        assertThat(result.getHashtag()).isEqualTo(findArticle.getHashtag());
        assertThat(result.getCreatedBy()).isEqualTo(findArticle.getCreatedBy());
    }

    @DisplayName("[성공] 게시글 검색조건 페이징 조회 테스트 - 일부 조건")
    @Test
    void givenSearchCondiAndPageable_whenArticles_thenWorksFine() {
        // given
        var createdBy = "Ethelin";
        var hashtag = "#Computers";
        var condition = ArticleCommand.SearchCondition
                .builder()
                .hashtag(hashtag)
                .createdBy(createdBy)
                .build();

        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));

        // when
        var result = articleQueryService.articles(condition, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @DisplayName("[성공] 게시글 검색조건 페이징 조회 테스트 - 모든 조건 포함")
    @Test
    void givenAllSearchCondiAndPageable_whenArticles_thenWorksFine() {
        // given
        var createdBy = "Jania";
        var hashtag = "#Garden";
        var title = "et eros vestibulum";
        var createdAt = LocalDateTime.of(2022, 1, 17, 11, 56);
        var condition = ArticleCommand.SearchCondition
                .builder()
                .title(title)
                .hashtag(hashtag)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .build();

        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));

        // when
        var result = articleQueryService.articles(condition, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("[성공] 전체 댓글 리스트 조회 테스트 - 검색조건 없음")
    @Test
    void givenNoneCondition_whenArticleList_thenSelectAllComments() {
        // given
        var condition = ArticleCommand.SearchCondition.builder().build();

        // when
        var result = articleQueryService.articleList(condition);

        // then
        assertThat(result.size()).isEqualTo(200);
        assertThat(result.get(0).getArticleId()).isEqualTo(200L);
    }
}