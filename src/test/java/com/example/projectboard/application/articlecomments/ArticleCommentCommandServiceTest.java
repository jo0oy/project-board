package com.example.projectboard.application.articlecomments;

import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleCommentCommandServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private ArticleCommentRepository articleCommentRepository;
    @Autowired
    private ArticleCommentCommandService sut;

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][service] 게시글 댓글 등록 테스트")
    @Test
    void givenRegisterReq_WhenRegisterComment_WorksFine() {
        // given
        var articleId = 3L;
        var content = "새로운 댓글 등록 테스트입니다.";
        var username = "jo0oy";
        var req = ArticleCommentCommand.RegisterReq
                .builder()
                .articleId(articleId)
                .commentBody(content)
                .build();

        // when
        var result = sut.registerComment(username, req);

        // then
        assertThat(result.getArticleId()).isEqualTo(articleId);
        assertThat(result.getCommentBody()).isEqualTo(content);
    }

    @DisplayName("[실패][service] 게시글 댓글 등록 테스트 - 존재하지 않는 유저")
    @Test
    void givenRegisterReqWithNotRegisteredUsername_WhenRegisterComment_ThenThrowsException() {
        // given
        var articleId = 3L;
        var content = "새로운 댓글 등록 테스트입니다.";
        var username = "noneUser";
        var req = ArticleCommentCommand.RegisterReq
                .builder()
                .articleId(articleId)
                .commentBody(content)
                .build();

        // when & then
        Assertions.assertThatThrownBy(() -> sut.registerComment(username, req))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("유저를 찾을 수 없습니다. username=" + username);
    }

    @DisplayName("[성공][service] 게시글 댓글 수정 테스트 - 수정 권한 있는 사용자")
    @Test
    void givenUpdateReqWithAuthorizedUsername_WhenUpdate_WorksFine() {
        // given
        var updateCommentId = 1L;
        var username = "yooj";
        var updateContent = "업데이트할 댓글 내용입니다.";
        var req = ArticleCommentCommand.UpdateReq
                .builder()
                .commentBody(updateContent)
                .build();

        // when
        sut.update(updateCommentId, username, req);
        em.clear();

        // then
        var updatedComment = articleCommentRepository.findById(updateCommentId).orElse(null);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getCommentBody()).isEqualTo(updateContent);
    }

    @DisplayName("[실패][service] 게시글 댓글 수정 테스트 - 수정 권한 없는 사용자")
    @Test
    void givenUpdateReqWithNotAuthorizedUsername_WhenUpdate_ThenThrowsException() {
        // given
        var updateCommentId = 3L;
        var username = "yooj";
        var updateContent = "업데이트할 댓글 내용입니다.";
        var req = ArticleCommentCommand.UpdateReq
                .builder()
                .commentBody(updateContent)
                .build();

        // when & then
        Assertions.assertThatThrownBy(() -> sut.update(updateCommentId, username, req))
                .isInstanceOf(NoAuthorityToUpdateDeleteException.class)
                .hasMessage("수정/삭제 권한이 없는 사용자입니다.");
    }

    @DisplayName("[실패][service] 게시글 댓글 삭제 테스트 - 삭제 권한 없는 사용자")
    @Test
    void givenCommentIdWithNotAuthorizedUsername_WhenDelete_ThenThrowsException() {
        // given
        var commentId = 5L;
        var username = "yooj";

        // when & then
        Assertions.assertThatThrownBy(() -> sut.delete(commentId, username))
                .isInstanceOf(NoAuthorityToUpdateDeleteException.class)
                .hasMessage("수정/삭제 권한이 없는 사용자입니다.");
    }

    @DisplayName("[성공][service] 게시글 댓글 삭제 테스트 - 삭제 권한 있는 사용자")
    @Test
    void givenCommentIdWithAuthorizedUsername_WhenDelete_WorksFine() {
        // given
        var commentId = 6L;
        var username = "jo0oy";
        var beforeDeleteTotal = articleCommentRepository.count();

        // when
        sut.delete(commentId, username);
        em.clear();

        // then
        assertThat(articleCommentRepository.count()).isEqualTo(beforeDeleteTotal - 1);
    }
}
