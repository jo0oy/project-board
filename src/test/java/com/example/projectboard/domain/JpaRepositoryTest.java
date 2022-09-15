package com.example.projectboard.domain;

import com.example.projectboard.config.JpaConfig;
import com.example.projectboard.config.QuerydslConfig;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.domain.articles.Article;
import com.example.projectboard.domain.articles.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA 연결 테스트")
@Import({JpaConfig.class, QuerydslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class JpaRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository articleCommentRepository;

    @Test
    @DisplayName("[성공] select 테스트")
    void givenTestData_WhenSelect_thenWorksFine() {
        //given

        //when
        var articles = articleRepository.findAll();

        //then
        assertThat(articles)
                .isNotNull()
                .hasSize(200);
    }

    @Test
    @DisplayName("[성공] insert 테스트")
    void whenInserting_thenWorksFine() {
        //given
        var previousArticlesCnt = articleRepository.count();

        //when
        articleRepository.save(
                Article.ArticleWithHashtag()
                        .title("new article title")
                        .content("new article content!!!")
                        .hashtag("#hashtag")
                        .build()
        );

        //then
        assertThat(articleRepository.count())
                .isEqualTo(previousArticlesCnt + 1);
    }

    @Test
    @DisplayName("[성공] update 테스트")
    void givenTestData_whenUpdating_thenWorksFine() {
        //given
        var article = articleRepository.findById(1L).orElseThrow();
        var updateHashtag = "#springboot";
        article.update(null, null, updateHashtag);

        //when
        var updatedArticle = articleRepository.saveAndFlush(article);

        //then
        assertThat(updatedArticle)
                .hasFieldOrPropertyWithValue("hashtag", updateHashtag);
    }

    @Test
    @DisplayName("[성공] delete 테스트")
    void givenTestData_whenDeleting_thenWorksFine() {
        //given
        var article = articleRepository.findById(1L).orElseThrow();
        var previousArticleCnt = articleRepository.count();
        var comments = articleCommentRepository.findByArticleId(1L);
        var previousCommentCnt = articleCommentRepository.count();

        //when
        articleCommentRepository.deleteAll(comments);
        articleRepository.delete(article);

        //then
        assertThat(articleRepository.count())
                .isEqualTo(previousArticleCnt - 1);

        assertThat(articleCommentRepository.count())
                .isEqualTo(previousCommentCnt - comments.size());
    }
}


