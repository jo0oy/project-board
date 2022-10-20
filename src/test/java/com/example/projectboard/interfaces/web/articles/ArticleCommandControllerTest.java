package com.example.projectboard.interfaces.web.articles;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.domain.articles.ArticleRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(FormDataEncoder.class)
@SpringBootTest
@AutoConfigureMockMvc
class ArticleCommandControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private FormDataEncoder encoder;

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][POST] 게시글 등록 테스트 - 인증된 사용자")
    @Test
    void givenRegisterReq_WhenPostMapping_ThenRedirectToMainPage() throws Exception {

        //given
        var title = "새로운 글 제목";
        var content = "새로운 글 내용입니다.";
        var hashtag = "#newArticle";
        var registerForm = ArticleDto.RegisterForm.builder()
                .title(title)
                .content(content)
                .hashtagContent(hashtag)
                .build();

        var beforeRegister = articleRepository.count();

        //when & then
        mvc.perform(post("/articles/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(registerForm))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles"))
                .andExpect(view().name("redirect:/articles"));

        assertThat(articleRepository.count()).isEqualTo(beforeRegister + 1);
    }

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][PUT] 게시글 수정 테스트 - 인증된 사용자(작성자)")
    @Test
    void givenArticleIdAndUpdateReq_WhenPutMapping_ThenRedirectToDetailPage() throws Exception {

        //given
        var articleId = 2L;
        var updateTitle = "수정한 제목입니다.";
        var updateContent = "수정한 내용입니다.";
        var updateHashtag = "#update";

        var updateReq = ArticleDto.UpdateForm.builder()
                .articleId(articleId)
                .title(updateTitle)
                .content(updateContent)
//                .hashtag(updateHashtag)
                .build();

        //when & then
        mvc.perform(put("/articles/" + articleId + "/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(updateReq))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        var updatedArticle = articleRepository.findById(articleId).orElse(null);

        assertThat(updatedArticle).isNotNull();
        assertThat(updatedArticle.getTitle()).isEqualTo(updateTitle);
        assertThat(updatedArticle.getContent()).isEqualTo(updateContent);
//        assertThat(updatedArticle.getHashtag()).isEqualTo(updateHashtag);
    }

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][PUT] 게시글 수정 테스트 - 수정 권한이 없는 인증된 사용자")
    @Test
    void givenNoAuthorityToUpdateUsername_WhenPutMapping_ThenShowErrorPage() throws Exception {

        //given
        var articleId = 1L;
        var updateTitle = "수정한 제목입니다.";
        var updateContent = "수정한 내용입니다.";
        var updateHashtag = "#update";

        var updateForm = ArticleDto.UpdateForm.builder()
                .articleId(articleId)
                .title(updateTitle)
                .content(updateContent)
//                .hashtag(updateHashtag)
                .build();

        //when & then
        var mvcResult = mvc.perform(put("/articles/" + articleId + "/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(updateForm))
                .with(csrf())
        ).andExpect(status().isForbidden()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);
    }

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][PUT] 게시글 수정 테스트 - 존재하지 않는 게시글")
    @Test
    void givenNonExistArticleIdAndUpdateReq_WhenPutMapping_ThenShowErrorPage() throws Exception {

        //given
        var articleId = 400L;
        var updateTitle = "수정한 제목입니다.";
        var updateContent = "수정한 내용입니다.";
        var updateHashtag = "#update";

        var updateReq = ArticleDto.UpdateForm.builder()
                .articleId(articleId)
                .title(updateTitle)
                .content(updateContent)
//                .hashtag(updateHashtag)
                .build();

        //when & then
        var mvcResult = mvc.perform(put("/articles/" + articleId + "/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(updateReq))
                .with(csrf())
        ).andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(EntityNotFoundException.class);
    }

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][DELETE] 게시글 삭제 테스트")
    @Test
    void givenArticleId_WhenDeleteMapping_ThenRedirectToMainPage() throws Exception {

        //given
        var articleId = 4L;
        var beforeDeleteTotal = articleRepository.count();

        //when & then
        mvc.perform(delete("/articles/" + articleId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles"))
                .andExpect(view().name("redirect:/articles"));

        assertThat(beforeDeleteTotal).isEqualTo(articleRepository.count() + 1);
    }

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][PUT] 게시글 삭제 테스트 - 존재하지 않는 게시글")
    @Test
    void givenNonExistArticleId_WhenDeleteMapping_ThenShowErrorPage() throws Exception {

        //given
        var articleId = 400L;

        //when & then
        var mvcResult = mvc.perform(delete("/articles/" + articleId).with(csrf()))
                .andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(EntityNotFoundException.class);
    }

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][PUT] 게시글 삭제 테스트 - 삭제 권한이 없는 인증된 사용자")
    @Test
    void givenNoAuthorityToUpdateUsername_WhenDeleteMapping_ThenShowErrorPage() throws Exception {

        //given
        var articleId = 1L;

        //when & then
        var mvcResult = mvc.perform(delete("/articles/" + articleId).with(csrf()))
                .andExpect(status().isForbidden()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);
    }
}
