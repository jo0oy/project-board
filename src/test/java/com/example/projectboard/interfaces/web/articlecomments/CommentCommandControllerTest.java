package com.example.projectboard.interfaces.web.articlecomments;

import com.example.projectboard.application.articlecomments.ArticleCommentCommandService;
import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.config.TestSecurityConfig;
import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDto;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDtoMapper;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDtoMapperImpl;
import com.example.projectboard.interfaces.dto.articles.ArticleDto;
import com.example.projectboard.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({FormDataEncoder.class, TestSecurityConfig.class})
@ComponentScan(basePackageClasses = {ArticleCommentDtoMapper.class, ArticleCommentDtoMapperImpl.class})
@AutoConfigureMockMvc
@WebMvcTest(controllers = CommentCommandController.class)
class CommentCommandControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FormDataEncoder encoder;

    @SpyBean
    private ArticleCommentDtoMapper commentDtoMapper;

    @MockBean
    private ArticleCommentCommandService commentCommandService;

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][POST] 댓글 등록 테스트 - 인증된 사용자")
    @Test
    void givenRegisterReq_WhenPostMapping_ThenRedirectToDetailPage() throws Exception {
        //given
        var articleId = 1L;
        var content = "새로운 댓글 내용입니다.";
        var article = ArticleDto.ArticleWithCommentsResponse.builder().build();
        var registerForm = getRegisterForm(articleId, content);

        given(commentCommandService.registerComment(anyString(), any(ArticleCommentCommand.RegisterReq.class)))
                .willReturn(any(ArticleCommentInfo.MainInfo.class));

        //when & then
        mvc.perform(post("/article-comments")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr("article", article)
                        .content(encoder.encode(registerForm))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        then(commentCommandService).should().registerComment(anyString(), any(ArticleCommentCommand.RegisterReq.class));
    }

    @DisplayName("[실패][controller][POST] 댓글 등록 테스트 - 미인증된 사용자일 경우 로그인 페이지로 이동")
    @Test
    void givenRegisterReqWithUnAuthenticatedUser_WhenPostMapping_ThenRedirectsToLoginPage() throws Exception {
        //given
        var articleId = 1L;
        var content = "새로운 댓글 내용입니다.";
        var registerReq = getRegisterReq(articleId, content, null);

        //when & then
        mvc.perform(post("/article-comments")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(registerReq))
                .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][POST] 대댓글 등록 테스트 - 인증된 사용자")
    @Test
    void givenRegisterChildForm_WhenPostMapping_ThenRedirectToDetailPage() throws Exception {
        //given
        var articleId = 4L;
        var parentId = 3L;
        var content = "대댓글 등록 내용입니다.";
        var article = ArticleDto.ArticleWithCommentsResponse.builder().build();
        var registerForm = getRegisterChildForm(articleId, content, parentId);

        given(commentCommandService.registerComment(anyString(), any(ArticleCommentCommand.RegisterReq.class)))
                .willReturn(any(ArticleCommentInfo.MainInfo.class));

        //when & then
        mvc.perform(post("/article-comments/child")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr("article", article)
                        .content(encoder.encode(registerForm))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        then(commentCommandService).should().registerComment(anyString(), any(ArticleCommentCommand.RegisterReq.class));
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][PUT] 댓글 수정 테스트 - 인증된 사용자[본인]")
    @Test
    void givenCommentIdAndUpdateReq_WhenPutMapping_ThenRedirectToDetailPage() throws Exception {
        //given
        var commentId = 2L; // createdBy 'userTest'
        var articleId = 3L;
        var updateCommentBody = "수정한 내용입니다.";

        var updateForm = getUpdateForm(articleId, updateCommentBody);

        willDoNothing().given(commentCommandService)
                .update(anyLong(), anyString(), any(ArticleCommentCommand.UpdateReq.class));

        //when & then
        mvc.perform(put("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(updateForm))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        then(commentCommandService).should()
                .update(anyLong(), anyString(), any(ArticleCommentCommand.UpdateReq.class));
    }

    @DisplayName("[실패][controller][PUT] 댓글 수정 테스트 - 인증되지 않은 사용자")
    @Test
    void givenNotAuthenticatedUser_WhenPutMapping_ThenRedirectToLoginPage() throws Exception {
        //given
        var commentId = 2L; // createdBy 'userTest'
        var articleId = 3L;
        var updateForm = "수정한 내용입니다.";

        var updateReq = getUpdateForm(articleId, updateForm);

        //when & then
        mvc.perform(put("/article-comments/" + commentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(updateReq))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        then(commentCommandService).shouldHaveNoInteractions();
    }

    @WithUserDetails(value = "userTest2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][PUT] 댓글 수정 테스트 - 수정 권한없는 인증된 사용자")
    @Test
    void givenCommentIdAndUpdateReqWithForbiddenUsername_WhenPutMapping_ThenReturnsForbiddenError() throws Exception {
        //given
        var commentId = 2L; // createdBy 'userTest'
        var articleId = 3L;
        var updateForm = "수정한 내용입니다.";

        var updateReq = getUpdateForm(articleId, updateForm);

        willThrow(NoAuthorityToUpdateDeleteException.class).given(commentCommandService)
                .update(anyLong(), anyString(), any(ArticleCommentCommand.UpdateReq.class));

        //when & then
        var mvcResult = mvc.perform(put("/article-comments/" + commentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(updateReq))
                .with(csrf())
        ).andExpect(status().isForbidden()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);

        then(commentCommandService).should()
                .update(anyLong(), anyString(), any(ArticleCommentCommand.UpdateReq.class));
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][PUT] 댓글 수정 테스트 - 존재하지 않는 댓글")
    @Test
    void givenNonExistCommentIdAndUpdateReq_WhenPutMapping_ThenReturnsClientError() throws Exception {
        //given
        var commentId = 1500L;
        var articleId = 2L;
        var updateCommentBody = "수정한 댓글 내용입니다.";

        var updateForm = getUpdateForm(articleId, updateCommentBody);

        willThrow(EntityNotFoundException.class).given(commentCommandService)
                .update(anyLong(), anyString(), any(ArticleCommentCommand.UpdateReq.class));

        //when & then
        var mvcResult = mvc.perform(put("/article-comments/" + commentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(updateForm))
                .with(csrf())
        ).andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(EntityNotFoundException.class);
        then(commentCommandService).should()
                .update(anyLong(), anyString(), any(ArticleCommentCommand.UpdateReq.class));
    }

    @WithUserDetails(value = "userTest2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][DELETE] 댓글 삭제 테스트 - 인증된 사용자[본인]")
    @Test
    void givenCommentId_WhenDeleteMapping_ThenRedirectToDetailPage() throws Exception {
        //given
        var commentId = 1L;
        var articleId = 2L;

        willDoNothing().given(commentCommandService).delete(anyLong(), anyString());

        //when & then
        mvc.perform(delete("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(Map.of("articleId", articleId)))
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        then(commentCommandService).should().delete(anyLong(), anyString());
    }

    @WithUserDetails(value = "adminTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][DELETE] 댓글 삭제 테스트 - 관리자 계정")
    @Test
    void givenCommentIdWithAdminAccount_WhenDeleteMapping_ThenRedirectToDetailPage() throws Exception {
        //given
        var commentId = 4L;
        var articleId = 5L;

        willDoNothing().given(commentCommandService).delete(anyLong(), anyString());

        //when & then
        mvc.perform(delete("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(Map.of("articleId", articleId)))
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        then(commentCommandService).should().delete(anyLong(), anyString());
    }

    @WithUserDetails(value = "userTest2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][DELETE] 댓글 삭제 테스트 - 삭제 권한이 없는 인증된 사용자")
    @Test
    void givenCommentIdWithForbiddenUsername_WhenDeleteMapping_ThenReturnsForbiddenError() throws Exception {
        //given
        var commentId = 6L;
        var articleId = 7L;

        willThrow(NoAuthorityToUpdateDeleteException.class).given(commentCommandService)
                .delete(anyLong(), anyString());

        //when & then
        var mvcResult = mvc.perform(delete("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(Map.of("articleId", articleId)))
                        .with(csrf())
                )
                .andExpect(status().isForbidden()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);
        then(commentCommandService).should().delete(anyLong(), anyString());
    }

    @DisplayName("[실패][controller][DELETE] 게시글 삭제 테스트 - 인증되지 않은 사용자")
    @Test
    void givenNotAuthenticatedUser_WhenDeleteMapping_ThenRedirectToLoginPage() throws Exception {
        //given
        var commentId = 1500L;
        var articleId = 1L;

        //when & then
        var mvcResult = mvc.perform(delete("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(Map.of("articleId", articleId)))
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        then(commentCommandService).shouldHaveNoInteractions();
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][DELETE] 게시글 삭제 테스트 - 존재하지 않는 댓글")
    @Test
    void givenNonExistCommentId_WhenDeleteMapping_ThenReturnsClientError() throws Exception {
        //given
        var commentId = 1500L;
        var articleId = 1L;

        willThrow(EntityNotFoundException.class).given(commentCommandService)
                .delete(anyLong(), anyString());

        //when & then
        var mvcResult = mvc.perform(delete("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(Map.of("articleId", articleId)))
                        .with(csrf())
                )
                .andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(EntityNotFoundException.class);
        then(commentCommandService).should().delete(anyLong(), anyString());
    }

    private static ArticleCommentDto.RegisterReq getRegisterReq(Long articleId, String content, Long parentId) {
        return ArticleCommentDto.RegisterReq.builder()
                .articleId(articleId)
                .commentBody(content)
                .parentId(parentId)
                .build();
    }

    private static ArticleCommentDto.RegisterForm getRegisterForm(long articleId, String content) {
        return ArticleCommentDto.RegisterForm.builder()
                .parentArticleId(articleId)
                .commentBody(content)
                .build();
    }

    private static ArticleCommentDto.RegisterChildForm getRegisterChildForm(Long articleId, String content, Long parentId) {
        return ArticleCommentDto.RegisterChildForm.builder()
                .articleId(articleId)
                .childCommentBody(content)
                .parentCommentId(parentId)
                .build();
    }

    private static ArticleCommentDto.UpdateForm getUpdateForm(long articleId, String updateCommentBody) {
        return ArticleCommentDto.UpdateForm.builder()
                .parentArticleId(articleId)
                .updateCommentBody(updateCommentBody)
                .build();
    }
}
