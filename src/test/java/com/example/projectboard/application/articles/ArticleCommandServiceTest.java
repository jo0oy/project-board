package com.example.projectboard.application.articles;

import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleCommandServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private ArticleCommandService sut;
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository articleCommentRepository;

    @DisplayName("[성공][service] 게시물 등록 테스트")
    @Test
    void givenRegisterReq_whenRegisterArticle_thenWorksFine() {
        // given
        var title = "새 게시글 제목";
        var content = "새 게시글 내용입니다!!";
        var hashtag = "#newPost";
        var req = ArticleCommand.RegisterReq.builder()
                .title(title)
                .content(content)
                .hashtag(hashtag)
                .build();

        // when
        var result = sut.registerArticle(req);

        // then
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getHashtag()).isEqualTo(hashtag);
    }

    @DisplayName("[성공][service] 게시물 수정 테스트")
    @Test
    void givenUpdateReq_whenUpdate_thenArticleUpdated() {
        // given
        var articleId = 5L;
        var updateTitle = "수정할 게시글 제목";
        var updateContent = "수정할 게시글 내용입니다.";
        var req = ArticleCommand.UpdateReq
                .builder()
                .title(updateTitle)
                .content(updateContent)
                .build();

        // when
        sut.update(articleId, req);
        em.clear();

        // then
        System.out.println("==================AFTER WHEN EXECUTED==================");
        var findArticle = articleRepository.findById(articleId).orElse(null);

        assertThat(findArticle).isNotNull();
        assertThat(findArticle.getTitle()).isEqualTo(updateTitle);
        assertThat(findArticle.getContent()).isEqualTo(updateContent);
    }

    @DisplayName("[성공][service] 게시물 삭제 테스트")
    @Test
    void givenArticleId_whenDelete_thenDeleteCommentsAndArticle() {
        // given
        var articleId = 1L;
        var beforeDeleteCommentListSize = articleCommentRepository.findByArticleId(articleId).size();

        // when
        sut.delete(articleId);

        // then
        System.out.println("==================AFTER WHEN EXECUTED==================");
        var findArticle = articleRepository.findById(articleId);
        var comments = articleCommentRepository.findByArticleId(articleId);

        assertThat(findArticle).isNotPresent();
        assertThat(comments.size()).isEqualTo(0);
        assertThat(beforeDeleteCommentListSize).isNotEqualTo(comments.size());
    }
}
