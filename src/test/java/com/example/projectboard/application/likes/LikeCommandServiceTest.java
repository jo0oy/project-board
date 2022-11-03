package com.example.projectboard.application.likes;

import com.example.projectboard.domain.likes.LikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LikeCommandServiceTest {

    @Autowired
    private LikeCommandService sut;

    @Autowired
    private LikeRepository likeRepository;

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][service] '좋아요' 누르기 기능 테스트 - 눌린적 없는 푸쉬")
    @Test
    void givenArticleIdAndUsername_whenPushLike_thenSavesNewLikeEntity() {
        //given
        var articleId = 1L;
        var principalUsername = "userTest";

        //when
        sut.pushLike(articleId, principalUsername);

        //then
        var findLike = likeRepository.findByArticle_IdAndUserAccount_Username(articleId, principalUsername);
        assertThat(findLike).isPresent();
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][service] '좋아요' 누르기 기능 테스트 - 이미 눌렸던 '좋아요' 푸쉬")
    @Test
    void givenAlreadyPushedArticleIdAndUsername_whenPushLike_thenDeleteLikeEntity() {
        //given
        var articleId = 2L;
        var principalUsername = "userTest";

        sut.pushLike(articleId, principalUsername);

        //when
        sut.pushLike(articleId, principalUsername);

        //then
        var findLike = likeRepository.findByArticle_IdAndUserAccount_Username(articleId, principalUsername);
        assertThat(findLike).isNotPresent();
    }
}
