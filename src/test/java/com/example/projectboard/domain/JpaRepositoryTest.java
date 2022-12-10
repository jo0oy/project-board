package com.example.projectboard.domain;

import com.example.projectboard.config.QuerydslConfig;
import com.example.projectboard.domain.articlecomments.ArticleComment;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.domain.articles.Article;
import com.example.projectboard.domain.articles.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.COLLECTION;

@DisplayName("JPA 연결 테스트")
@Import({JpaRepositoryTest.JpaConfig.class, QuerydslConfig.class})
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
                .hasSize(30);
    }

    @Test
    @DisplayName("[성공] insert 테스트")
    void whenInserting_thenWorksFine() {
        //given
        var previousArticlesCnt = articleRepository.count();
        var title = "new article title";
        var content = "new article content!!!";

        //when
        articleRepository.save(
                Article.of(title, content, 1L)
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
        var updateContent = "수정된 게시글 내용입니다.";
        article.update(null, updateContent);

        //when
        var updatedArticle = articleRepository.saveAndFlush(article);

        //then
        assertThat(updatedArticle.getContent())
                .isEqualTo(updateContent);
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

    @DisplayName("[성공] 대댓글 조회 테스트")
    @Test
    void givenParentId_whenSelecting_thenReturnsChildComments() {
        //given
        var parentId = 1L;

        //when
        var parentComment = articleCommentRepository.findById(parentId);

        //then
        assertThat(parentComment).get()
                .hasFieldOrPropertyWithValue("parent", null)
                .extracting("childs", COLLECTION)
                        .hasSize(4);
    }

    @DisplayName("[성공] 댓글에 대댓글 삽입 테스트")
    @Test
    void givenParentComment_whenSaving_thenInsertsChildComment() {
        // Given
        var parentComment = articleCommentRepository.getReferenceById(1L);
        var childComment = ArticleComment.of(
                "new CommentBody",
                parentComment.getArticle(),
                parentComment.getUserId()
        );

        // When
        parentComment.addChild(childComment);
        articleCommentRepository.flush();

        // Then
        assertThat(articleCommentRepository.findById(1L)).get()
                .hasFieldOrPropertyWithValue("parent", null)
                .extracting("childs", COLLECTION)
                .hasSize(5);
    }

    @DisplayName("댓글 삭제와 대댓글 전체 연동 삭제 테스트: 기본 JPA 삭제 메서드 사용")
    @Test
    void givenArticleCommentHavingChildComments_whenDeletingParentComment_thenDeletesEveryComment() {
        // Given
        var parentComment = articleCommentRepository.getReferenceById(1L);
        long previousArticleCommentCount = articleCommentRepository.count();

        // When
        articleCommentRepository.delete(parentComment);

        // Then
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - 5); // 테스트 댓글 + 대댓글 4개
    }

    @DisplayName("[성공] 댓글 삭제와 대댓글 전체 연동 삭제 테스트 - 댓글 ID, 벌크 삭제")
    @Test
    void givenArticleCommentIdHavingChildComments_whenDeletingParentComment_thenDeletesEveryComment() {
        // Given
        long previousArticleCommentCount = articleCommentRepository.count();

        // When
        articleCommentRepository.deleteByParentId(1L);
        articleCommentRepository.deleteById(1L);

        // Then
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - 5); // 테스트 댓글 + 대댓글 4개
    }


    @EnableJpaAuditing
    @TestConfiguration
    public static class JpaConfig {
        @Bean
        public AuditorAware<String> auditorAware() {

            return () -> Optional.of("testUser");
        }
    }
}
