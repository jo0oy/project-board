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
    private ArticleQueryService sut;

    @Autowired
    private ArticleRepository articleRepository;

    @DisplayName("[성공][service] 게시글 단건 조회 테스트")
    @Test
    void givenArticleId_whenGetArticle_thenWorksFine() {
        // given
        var articleId = 1L;

        // when
        var result = sut.getArticle(articleId);

        var findArticle = articleRepository.findById(articleId).orElse(null);

        // then
        assertThat(result).isNotNull();
        assertThat(findArticle).isNotNull();
        assertThat(result.getHashtagInfos().size()).isEqualTo(3);
        assertThat(result.getCreatedBy()).isEqualTo(findArticle.getCreatedBy());
    }

    @DisplayName("[성공][service] 게시글 검색조건 페이징 조회 테스트 - 일부 조건")
    @Test
    void givenSearchConditionAndPageable_whenArticles_thenWorksFine() {
        // given
        var createdBy = "userTest2";
        var condition = ArticleCommand.SearchCondition
                .builder()
                .createdBy(createdBy)
                .build();

        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));

        // when
        var result = sut.articles(condition, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(15);
    }

    @DisplayName("[성공][service] 게시글 검색조건 페이징 조회 테스트 - 모든 조건 포함")
    @Test
    void givenAllSearchConditionAndPageable_whenArticles_thenWorksFine() {
        // given
        var createdBy = "userTest2";
        var hashtag = "#Crimson";
        var title = "eu";
        var createdAt = LocalDateTime.of(2022, 10, 20, 23, 05);
        var condition = ArticleCommand.SearchCondition
                .builder()
                .title(title)
                .hashtag(hashtag)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .build();

        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));

        // when
        var result = sut.articles(condition, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("[성공][service] 전체 댓글 리스트 조회 테스트 - 검색조건 없음")
    @Test
    void givenNoneCondition_whenArticleList_thenSelectAllComments() {
        // given
        var condition = ArticleCommand.SearchCondition.builder().build();

        // when
        var result = sut.articleList(condition);

        // then
        assertThat(result.size()).isEqualTo(30);
        assertThat(result.get(0).getArticleId()).isEqualTo(1L);
    }
}
