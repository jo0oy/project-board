package com.example.projectboard.application.articles;

import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import com.example.projectboard.common.util.HashtagContentUtil;
import com.example.projectboard.config.JpaConfig;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaConfig.class)
@SpringBootTest
class ArticleCommandServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ArticleCommandService sut;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository articleCommentRepository;

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][service] 게시물 등록 테스트")
    @Test
    void givenRegisterReq_whenRegisterArticle_thenWorksFine() {
        // given
        var title = "새 게시글 제목";
        var content = "새 게시글 내용입니다!!";
        var hashtagContent = "#newPost,#hashtag,#oohlala,#Happy, #happy";
        var username = "userTest";
        var req = ArticleCommand.RegisterReq.builder()
                .title(title)
                .content(content)
                .hashtagNames(HashtagContentUtil.convertToList(hashtagContent))
                .build();

        // when
        var result = sut.registerArticle(username, req);

        // then
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getHashtagInfos().size()).isEqualTo(5);
    }

    @DisplayName("[실패][service] 게시물 등록 테스트 - 존재하지 않는 사용자")
    @Test
    void givenRegisterReqWithNotRegisteredUsername_whenRegisterArticle_thenThrowsException() {
        // given
        var title = "새 게시글 제목";
        var content = "새 게시글 내용입니다!!";
        var hashtagContent = "#newPost,#hashtag,#oohlala,#Happy, #happy";
        var username = "noneUser";
        var req = ArticleCommand.RegisterReq.builder()
                .title(title)
                .content(content)
                .hashtagNames(HashtagContentUtil.convertToList(hashtagContent))
                .build();

        // when & then
        Assertions.assertThatThrownBy(() -> sut.registerArticle(username, req))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("유저를 찾을 수 없습니다. username=" + username);
    }

    @Transactional
    @WithUserDetails(value = "userTest2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][service] 게시물 수정 테스트 - 수정 권한 있는 사용자")
    @Test
    void givenUpdateReqWithAuthorizedUsername_whenUpdate_thenArticleUpdated() {

        // given
        var articleId = 1L;
        var username = "userTest2";
        var updateTitle = "수정할 게시글 제목";
        var updateContent = "수정할 게시글 내용입니다.";
        var updateReq = ArticleCommand.UpdateReq
                .builder()
                .title(updateTitle)
                .content(updateContent)
                .hashtagNames(HashtagContentUtil.convertToList("#newyork,  #EmpireStates"))
                .build();

        // when
        sut.update(articleId, username, updateReq);
        em.flush();
        em.clear();

        // then
        System.out.println("==================AFTER WHEN EXECUTED==================");
        var findArticle = articleRepository.findById(articleId).orElse(null);

        assertThat(findArticle).isNotNull();
        assertThat(findArticle.getTitle()).isEqualTo(updateTitle);
        assertThat(findArticle.getContent()).isEqualTo(updateContent);
    }

    @DisplayName("[실패][service] 게시물 수정 테스트 - 수정 권한이 없는 사용자")
    @Test
    void givenUpdateReqWithNoneAuthorizedUsername_whenUpdate_thenThrowsException() {
        // given
        var articleId = 1L;
        var username = "userTest";
        var updateTitle = "수정할 게시글 제목";
        var updateContent = "수정할 게시글 내용입니다.";
        var req = ArticleCommand.UpdateReq
                .builder()
                .title(updateTitle)
                .content(updateContent)
                .build();

        // when & then
        Assertions.assertThatThrownBy(() -> sut.update(articleId, username, req))
                .isInstanceOf(NoAuthorityToUpdateDeleteException.class)
                .hasMessage("수정/삭제 권한이 없는 사용자입니다.");
    }

    @DisplayName("[성공][service] 게시물 삭제 테스트 - 삭제 권한 있는 사용자")
    @Test
    void givenArticleIdWithAuthorizedUsername_whenDelete_thenDeleteCommentsAndArticle() {
        // given
        var articleId = 30L;
        var username = "userTest";
        var beforeDeleteCommentListSize = articleCommentRepository.findByArticleId(articleId).size();

        // when
        sut.delete(articleId, username);

        // then
        System.out.println("==================AFTER WHEN EXECUTED==================");
        var findArticle = articleRepository.findById(articleId);
        var comments = articleCommentRepository.findByArticleId(articleId);

        assertThat(findArticle).isNotPresent();
        assertThat(comments.size()).isEqualTo(0);
        assertThat(beforeDeleteCommentListSize).isNotEqualTo(comments.size());
    }

    @DisplayName("[실패][service] 게시물 삭제 테스트 - 삭제 권한 없는 사용자")
    @Test
    void givenArticleIdWithNoneAuthorizedUsername_whenDelete_thenThrowsException() {
        // given
        var articleId = 3L;
        var username = "userTest";

        // when & then
        Assertions.assertThatThrownBy(() -> sut.delete(articleId, username))
                .isInstanceOf(NoAuthorityToUpdateDeleteException.class)
                .hasMessage("수정/삭제 권한이 없는 사용자입니다.");
    }
}
