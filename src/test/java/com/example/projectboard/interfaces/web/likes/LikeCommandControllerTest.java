package com.example.projectboard.interfaces.web.likes;

import com.example.projectboard.application.likes.LikeCommandService;
import com.example.projectboard.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@WebMvcTest(controllers = LikeCommandController.class)
class LikeCommandControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LikeCommandService likeCommandService;

    @Test
    @DisplayName("[실패][controller] '좋아요' 등록 테스트 - 인증되지 않은 사용자일 경우 로그인 페이지로 이동")
    void givenArticleIdAndPrincipalUsernameWithNotAuthenticatedUser_WhenPostingLike_thenRedirectToLoginPage() throws Exception {
        //given
        var articleId = 1L;
        var principalUsername = "userTest";

        willDoNothing().given(likeCommandService).pushLike(eq(articleId), eq(principalUsername));

        //when
        mvc.perform(post("/likes/" + articleId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        //then
        then(likeCommandService).shouldHaveNoInteractions();
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("[성공][controller] '좋아요' 등록 테스트")
    void givenArticleIdAndPrincipalUsername_WhenPostingLike_thenSaveLikeAndRedirectToDetailPage() throws Exception {
        //given
        var articleId = 1L;
        var principalUsername = "userTest";

        willDoNothing().given(likeCommandService).pushLike(eq(articleId), eq(principalUsername));

        //when
        mvc.perform(post("/likes/" + articleId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId));

        //then
        then(likeCommandService).should().pushLike(eq(articleId), eq(principalUsername));
    }

    @WithUserDetails(value = "userTest2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("[성공][controller] '좋아요' 등록 테스트 - 이미 눌렀던 '좋아요' 버튼")
    void givenAlreadyPushedLikeInfo_WhenPostingLike_thenDeleteLikeAndRedirectToDetailPage() throws Exception {
        //given
        var articleId = 2L;
        var principalUsername = "userTest2";

        willDoNothing().given(likeCommandService).pushLike(eq(articleId), eq(principalUsername));

        //when
        mvc.perform(post("/likes/" + articleId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId));

        //then
        then(likeCommandService).should().pushLike(eq(articleId), eq(principalUsername));
    }
}
