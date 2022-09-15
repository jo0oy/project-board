package com.example.projectboard.application.articlecomments;

import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleCommentCommandServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ArticleCommentRepository articleCommentRepository;

    @Autowired
    private ArticleCommentCommandService articleCommentCommandService;

    @DisplayName("[성공] 게시글 댓글 등록 테스트")
    @Test
    void givenRegisterReq_WhenRegisterComment_WorksFine() {
        // given
        var articleId = 3L;
        var content = "새로운 댓글 등록 테스트입니다.";
        var req = ArticleCommentCommand.RegisterReq
                .builder()
                .articleId(articleId)
                .content(content)
                .build();

        // when
        var result = articleCommentCommandService.registerComment(req);

        // then
        assertThat(result.getArticleId()).isEqualTo(articleId);
        assertThat(result.getContent()).isEqualTo(content);
    }

    @DisplayName("[성공] 게시글 댓글 수정 테스트")
    @Test
    void givenUpdateReq_WhenUpdate_WorksFine() {
        // given
        var updateCommentId = 5L;
        var updateContent = "업데이트할 댓글 내용입니다.";
        var req = ArticleCommentCommand.UpdateReq
                .builder()
                .content(updateContent)
                .build();

        // when
        articleCommentCommandService.update(updateCommentId, req);

        em.clear();

        var updatedComment = articleCommentRepository.findById(updateCommentId).orElse(null);

        // then
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getContent()).isEqualTo(updateContent);
    }
}