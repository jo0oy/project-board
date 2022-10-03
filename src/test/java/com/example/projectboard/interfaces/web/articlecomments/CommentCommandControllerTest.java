package com.example.projectboard.interfaces.web.articlecomments;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDto;
import com.example.projectboard.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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

    @DisplayName("[성공][controller][POST] 댓글 등록 테스트")
    @Test
    void givenRegisterReq_WhenPostMapping_ThenRedirectToDetailPage() throws Exception {

        //given
        var articleId = 1L;
        var content = "새로운 댓글 내용입니다.";
        var registerReq = ArticleCommentDto.RegisterReq.builder()
                .articleId(articleId)
                .content(content)
                .build();

        var beforeRegister = commentRepository.count();

        //when & then
        mvc.perform(post("/article-comments")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(registerReq))
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        assertThat(commentRepository.count()).isEqualTo(beforeRegister + 1);
    }

    @DisplayName("[성공][controller][PUT] 댓글 수정 테스트")
    @Test
    void givenCommentIdAndUpdateReq_WhenPutMapping_ThenRedirectToDetailPage() throws Exception {

        //given
        var commentId = 1L;
        var articleId = 2L;
        var updateContent = "수정한 내용입니다.";

        var updateReq = ArticleCommentDto.UpdateReq.builder()
                .content(updateContent)
                .build();

        //when & then
        mvc.perform(put("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .queryParam("content", updateContent)
                        .queryParam("articleId", String.valueOf(articleId))
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        var updatedComment = commentRepository.findById(commentId).orElse(null);

        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getContent()).isEqualTo(updateContent);
    }

    @DisplayName("[실패][controller][PUT] 댓글 수정 테스트 - 존재하지 않는 댓글")
    @Test
    void givenNonExistCommentIdAndUpdateReq_WhenPutMapping_ThenShowErrorPage() throws Exception {

        //given
        var commentId = 1500L;
        var articleId = 2L;
        var updateContent = "수정한 댓글 내용입니다.";

        var updateReq = ArticleCommentDto.UpdateReq.builder()
                .content(updateContent)
                .build();

        //when & then
        var mvcResult = mvc.perform(put("/article-comments/" + commentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(encoder.encode(updateReq))
                .content(encoder.encode(Map.of("articleId", articleId)))
        ).andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("[성공][controller][DELETE] 댓글 삭제 테스트")
    @Test
    void givenCommentId_WhenDeleteMapping_ThenRedirectToDetailPage() throws Exception {

        //given
        var commentId = 5L;
        var articleId = 6L;
        var beforeDeleteTotal = commentRepository.count();

        //when & then
        mvc.perform(delete("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(Map.of("articleId", articleId)))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId))
                .andExpect(view().name("redirect:/articles/" + articleId));

        assertThat(beforeDeleteTotal).isEqualTo(commentRepository.count() + 1);
    }

    @DisplayName("[실패][controller][DELETE] 게시글 삭제 테스트 - 존재하지 않는 댓글")
    @Test
    void givenNonExistCommentId_WhenDeleteMapping_ThenShowErrorPage() throws Exception {

        //given
        var commentId = 1500L;
        var articleId = 1L;

        //when & then
        var mvcResult = mvc.perform(delete("/article-comments/" + commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(Map.of("articleId", articleId)))
                )
                .andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(EntityNotFoundException.class);
    }
}
