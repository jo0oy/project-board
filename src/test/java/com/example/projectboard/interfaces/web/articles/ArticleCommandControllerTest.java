package com.example.projectboard.interfaces.web.articles;

import com.example.projectboard.application.articles.ArticleCommandService;
import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.config.TestSecurityConfig;
import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;
import com.example.projectboard.interfaces.dto.articles.ArticleDto;
import com.example.projectboard.interfaces.dto.articles.ArticleDtoMapper;
import com.example.projectboard.interfaces.dto.articles.ArticleDtoMapperImpl;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({FormDataEncoder.class, TestSecurityConfig.class})
@ComponentScan(basePackageClasses = {ArticleDtoMapper.class, ArticleDtoMapperImpl.class})
@AutoConfigureMockMvc
@WebMvcTest(controllers = ArticleCommandController.class)
class ArticleCommandControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FormDataEncoder encoder;

    @SpyBean
    private ArticleDtoMapper articleDtoMapper;

    @MockBean
    private ArticleCommandService articleCommandService;

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[??????][controller][POST] ????????? ?????? ????????? - ????????? ?????????")
    @Test
    void givenRegisterReq_WhenPostMapping_ThenRedirectToMainPage() throws Exception {

        //given
        var title = "????????? ??? ??????";
        var content = "????????? ??? ???????????????.";
        var hashtag = "#newArticle, #pink";
        var registerForm = ArticleDto.RegisterForm.builder()
                .title(title)
                .content(content)
                .hashtagContent(hashtag)
                .build();

        given(articleCommandService.registerArticleWithValidHashtags(anyString(), any(ArticleCommand.RegisterReq.class)))
                .willReturn(ArticleInfo.MainInfo.builder().build());

        //when & then
        mvc.perform(post("/articles/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(registerForm))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles"))
                .andExpect(view().name("redirect:/articles"));

        then(articleCommandService).should().registerArticleWithValidHashtags(anyString(), any(ArticleCommand.RegisterReq.class));
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[??????][controller][PUT] ????????? ?????? ????????? - ????????? ?????????(?????????)")
    @Test
    void givenArticleIdAndUpdateReq_WhenPutMapping_ThenRedirectToDetailPage() throws Exception {

        //given
        var articleId = 2L;
        var updateTitle = "????????? ???????????????.";
        var updateContent = "????????? ???????????????.";
        var updateHashtag = "#update, #hashtag";

        var updateForm = getUpdateForm(articleId, updateTitle, updateContent, updateHashtag);

        willDoNothing().given(articleCommandService)
                .update(eq(articleId), anyString(), any(ArticleCommand.UpdateReq.class));

        //when & then
        mvc.perform(put("/articles/" + articleId + "/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(updateForm))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        then(articleCommandService).should().update(eq(articleId), anyString(), any(ArticleCommand.UpdateReq.class));
    }

    @DisplayName("[??????][controller][PUT] ????????? ?????? ????????? - ???????????? ?????? ?????????")
    @Test
    void givenNotAuthenticatedUserWithUpdateReq_WhenPutMapping_ThenRedirectToLoginPage() throws Exception {
        // given
        var articleId = 2L;
        var updateTitle = "????????? ???????????????.";
        var updateContent = "????????? ???????????????.";
        var updateHashtag = "#update, #hashtag";

        var updateForm = getUpdateForm(articleId, updateTitle, updateContent, updateHashtag);

        // when & then
        mvc.perform(put("/articles/" + articleId + "/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(updateForm))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        then(articleCommandService).shouldHaveNoInteractions();
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[??????][controller][PUT] ????????? ?????? ????????? - ?????? ????????? ?????? ????????? ?????????")
    @Test
    void givenNoAuthorityToUpdateUsername_WhenPutMapping_ThenShowErrorPage() throws Exception {

        //given
        var articleId = 1L;
        var updateTitle = "????????? ???????????????.";
        var updateContent = "????????? ???????????????.";
        var updateHashtag = "#update";

        var updateForm = getUpdateForm(articleId, updateTitle, updateContent, updateHashtag);

        willThrow(NoAuthorityToUpdateDeleteException.class).given(articleCommandService)
                .update(eq(articleId), anyString(), any(ArticleCommand.UpdateReq.class));

        //when & then
        var mvcResult = mvc.perform(put("/articles/" + articleId + "/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(updateForm))
                .with(csrf())
        ).andExpect(status().isForbidden()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);
        then(articleCommandService).should().update(eq(articleId), anyString(), any(ArticleCommand.UpdateReq.class));
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[??????][controller][PUT] ????????? ?????? ????????? - ???????????? ?????? ?????????")
    @Test
    void givenNonExistArticleIdAndUpdateReq_WhenPutMapping_ThenShowErrorPage() throws Exception {

        //given
        var articleId = 400L;
        var updateTitle = "????????? ???????????????.";
        var updateContent = "????????? ???????????????.";
        var updateHashtag = "#update";

        var updateReq = getUpdateForm(articleId, updateTitle, updateContent, updateHashtag);

        willThrow(EntityNotFoundException.class).given(articleCommandService)
                .update(eq(articleId), anyString(), any(ArticleCommand.UpdateReq.class));

        //when & then
        var mvcResult = mvc.perform(put("/articles/" + articleId + "/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(updateReq))
                .with(csrf())
        ).andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(EntityNotFoundException.class);

        then(articleCommandService).should().update(eq(articleId), anyString(), any(ArticleCommand.UpdateReq.class));
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[??????][controller][DELETE] ????????? ?????? ?????????")
    @Test
    void givenArticleId_WhenDeleteMapping_ThenRedirectToMainPage() throws Exception {

        //given
        var articleId = 4L;

        willDoNothing().given(articleCommandService).delete(anyLong(), anyString());

        //when & then
        mvc.perform(delete("/articles/" + articleId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles"))
                .andExpect(view().name("redirect:/articles"));

        then(articleCommandService).should().delete(anyLong(), anyString());
    }

    @DisplayName("[??????][controller][DELETE] ????????? ?????? ????????? - ???????????? ?????? ?????????")
    @Test
    void givenNotAuthenticatedUser_WhenDeleteMapping_ThenRedirectToMainPage() throws Exception {
        //given
        var articleId = 4L;

        //when & then
        mvc.perform(delete("/articles/" + articleId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        then(articleCommandService).shouldHaveNoInteractions();
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[??????][controller][PUT] ????????? ?????? ????????? - ???????????? ?????? ?????????")
    @Test
    void givenNonExistArticleId_WhenDeleteMapping_ThenShowErrorPage() throws Exception {

        //given
        var articleId = 400L;

        willThrow(EntityNotFoundException.class).given(articleCommandService)
                .delete(anyLong(), anyString());

        //when & then
        var mvcResult = mvc.perform(delete("/articles/" + articleId).with(csrf()))
                .andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(EntityNotFoundException.class);

        then(articleCommandService).should().delete(anyLong(), anyString());
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[??????][controller][PUT] ????????? ?????? ????????? - ?????? ????????? ?????? ????????? ?????????")
    @Test
    void givenNoAuthorityToUpdateUsername_WhenDeleteMapping_ThenShowErrorPage() throws Exception {

        //given
        var articleId = 1L;

        willThrow(NoAuthorityToUpdateDeleteException.class).given(articleCommandService)
                .delete(anyLong(), anyString());

        //when & then
        var mvcResult = mvc.perform(delete("/articles/" + articleId).with(csrf()))
                .andExpect(status().isForbidden()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);

        then(articleCommandService).should().delete(anyLong(), anyString());
    }

    private static ArticleDto.UpdateForm getUpdateForm(long articleId, String updateTitle, String updateContent, String updateHashtag) {
        return ArticleDto.UpdateForm.builder()
                .articleId(articleId)
                .title(updateTitle)
                .content(updateContent)
                .hashtagContent(updateHashtag)
                .build();
    }
}
