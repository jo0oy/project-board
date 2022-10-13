package com.example.projectboard.interfaces.web.articlecomments;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDto;
import com.example.projectboard.interfaces.dto.articles.ArticleDto;
import com.example.projectboard.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(FormDataEncoder.class)
@AutoConfigureMockMvc
class CommentCommandControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ArticleCommentRepository commentRepository;

    @Autowired
    private FormDataEncoder encoder;

    @WithUserDetails(value = "user3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][POST] 댓글 등록 테스트 - 인증된 사용자")
    @Test
    void givenRegisterReq_WhenPostMapping_ThenRedirectToDetailPage() throws Exception {
        //given
        var articleId = 1L;
        var content = "새로운 댓글 내용입니다.";
        var article = ArticleDto.ArticleWithCommentsResponse.builder().build();
        var registerReq = ArticleCommentDto.RegisterForm.builder()
                .parentArticleId(articleId)
                .commentBody(content)
                .build();

        var beforeRegister = commentRepository.count();

        //when & then
        mvc.perform(post("/article-comments")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr("article", article)
                        .content(encoder.encode(registerReq))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        assertThat(commentRepository.count()).isEqualTo(beforeRegister + 1);
    }

    @DisplayName("[실패][controller][POST] 댓글 등록 테스트 - 미인증된 사용자일 경우 로그인 페이지로 이동")
    @Test
    void givenRegisterReqWithUnAuthenticatedUser_WhenPostMapping_ThenRedirectsToLoginPage() throws Exception {
        //given
        var articleId = 1L;
        var content = "새로운 댓글 내용입니다.";
        var registerReq = ArticleCommentDto.RegisterReq.builder()
                .articleId(articleId)
                .commentBody(content)
                .build();

        //when & then
        mvc.perform(post("/article-comments")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(registerReq))
                .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][PUT] 댓글 수정 테스트 - 인증된 사용자[본인]")
    @Test
    void givenCommentIdAndUpdateReq_WhenPutMapping_ThenRedirectToDetailPage() throws Exception {
        //given
        var commentId = 3L; // createdBy 'jo0oy'
        var articleId = 4L;
        var updateCommentBody = "수정한 내용입니다.";

        var updateReq = ArticleCommentDto.UpdateForm.builder()
                .parentArticleId(articleId)
                .updateCommentBody(updateCommentBody)
                .build();

        //when & then
        mvc.perform(put("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(updateReq))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        var updatedComment = commentRepository.findById(commentId).orElse(null);

        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getCommentBody()).isEqualTo(updateCommentBody);
    }

    @WithUserDetails(value = "user3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][PUT] 댓글 수정 테스트 - 수정 권한없는 인증된 사용자")
    @Test
    void givenCommentIdAndUpdateReqWithForbiddenUsername_WhenPutMapping_ThenReturnsForbiddenError() throws Exception {
        //given
        var commentId = 3L; // createdBy jo0oy
        var articleId = 4L;
        var updateForm = "수정한 내용입니다.";

        var updateReq = ArticleCommentDto.UpdateForm.builder()
                .parentArticleId(articleId)
                .updateCommentBody(updateForm)
                .build();

        //when & then
        var mvcResult = mvc.perform(put("/article-comments/" + commentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(updateReq))
                .with(csrf())
        ).andExpect(status().isForbidden()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);
    }

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][PUT] 댓글 수정 테스트 - 존재하지 않는 댓글")
    @Test
    void givenNonExistCommentIdAndUpdateReq_WhenPutMapping_ThenReturnsClientError() throws Exception {
        //given
        var commentId = 1500L;
        var articleId = 2L;
        var updateCommentBody = "수정한 댓글 내용입니다.";

        var updateForm = ArticleCommentDto.UpdateForm.builder()
                .parentArticleId(articleId)
                .updateCommentBody(updateCommentBody)
                .build();

        //when & then
        var mvcResult = mvc.perform(put("/article-comments/" + commentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(updateForm))
                .with(csrf())
        ).andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(EntityNotFoundException.class);
    }

    @WithUserDetails(value = "yooj", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][DELETE] 댓글 삭제 테스트 - 인증된 사용자[본인]")
    @Test
    void givenCommentId_WhenDeleteMapping_ThenRedirectToDetailPage() throws Exception {
        //given
        var commentId = 1L;
        var articleId = 2L;
        var beforeDeleteTotal = commentRepository.count();

        //when & then
        mvc.perform(delete("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(Map.of("articleId", articleId)))
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        assertThat(beforeDeleteTotal).isEqualTo(commentRepository.count() + 1);
    }

    @WithUserDetails(value = "admin1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][DELETE] 댓글 삭제 테스트 - 관리자 계정")
    @Test
    void givenCommentIdWithAdminAccount_WhenDeleteMapping_ThenRedirectToDetailPage() throws Exception {
        //given
        var commentId = 4L;
        var articleId = 5L;
        var beforeDeleteTotal = commentRepository.count();

        //when & then
        mvc.perform(delete("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(Map.of("articleId", articleId)))
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        assertThat(beforeDeleteTotal).isEqualTo(commentRepository.count() + 1);
    }

    @WithUserDetails(value = "user5", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][DELETE] 댓글 삭제 테스트 - 삭제 권한이 없는 인증된 사용자")
    @Test
    void givenCommentIdWithForbiddenUsername_WhenDeleteMapping_ThenReturnsForbiddenError() throws Exception {
        //given
        var commentId = 6L;
        var articleId = 7L;

        //when & then
        var mvcResult = mvc.perform(delete("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(Map.of("articleId", articleId)))
                        .with(csrf())
                )
                .andExpect(status().isForbidden()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);
    }

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][DELETE] 게시글 삭제 테스트 - 존재하지 않는 댓글")
    @Test
    void givenNonExistCommentId_WhenDeleteMapping_ThenReturnsClientError() throws Exception {
        //given
        var commentId = 1500L;
        var articleId = 1L;

        //when & then
        var mvcResult = mvc.perform(delete("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(Map.of("articleId", articleId)))
                        .with(csrf())
                )
                .andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(EntityNotFoundException.class);
    }
}
