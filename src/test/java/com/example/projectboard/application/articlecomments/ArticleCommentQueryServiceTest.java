package com.example.projectboard.application.articlecomments;

import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleCommentQueryServiceTest {

    @Autowired
    private ArticleCommentQueryService sut;
    @Autowired
    private ArticleCommentRepository articleCommentRepository;

    @DisplayName("[성공][service] 게시글 댓글 단건 조회")
    @Test
    void givenCommentId_whenGetComment_thenWorksFine() {
        // given
        var commentId = 1L;

        // when
        var result = sut.getComment(commentId);

        // then
        var findComment = articleCommentRepository.findById(commentId).orElse(null);
        assertThat(findComment).isNotNull();
        assertThat(result.getCommentBody()).isEqualTo(findComment.getCommentBody());
        assertThat(result.getArticleId()).isEqualTo(findComment.getArticle().getId());
    }

    @DisplayName("[성공][service] 해당 게시글에 작성된 댓글 리스트 조회 by articleId")
    @Test
    void givenArticleIdAndPageable_whenCommentsByArticleId_thenWorksFine() {
        // given
        var articleId = 2L;
        var articleCommentsTotal = 3;
        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));

        // when
        var result = sut.commentsByArticleId(articleId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(articleCommentsTotal);
        assertThat(result.getContent().get(0).getCommentId()).isGreaterThan(result.getContent().get(1).getCommentId());
        assertThat(result.getSize()).isEqualTo(20);
    }

    @DisplayName("[성공][service] 댓글 검색 페이징 결과 조회 - only by 작성일")
    @Test
    void givenCreatedAtAndPageable_whenComments_thenWorksFine() {
        // given
        var condition = ArticleCommentCommand.SearchCondition.builder()
                .createdAt(LocalDateTime.of(2022, 10, 21, 11, 0))
                .build();

        var pageSize = 20;
        var pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        // when
        var result = sut.comments(condition, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("[성공][service] 댓글 검색 페이징 결과 조회 - only by 작성자")
    @Test
    void givenCreatedByAndPageable_whenComments_thenWorksFine() {
        // given
        var condition = ArticleCommentCommand.SearchCondition.builder()
                .createdBy("user10")
                .build();

        var pageSize = 20;
        var pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        // when
        var result = sut.comments(condition, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @DisplayName("[성공][service] 댓글 검색(작성일 & 작성자) 페이징 결과 조회")
    @Test
    void givenSearchConditionAndPageable_whenComments_thenWorksFine() {
        // given
        var condition = ArticleCommentCommand.SearchCondition.builder()
                .createdAt(LocalDateTime.of(2022, 10, 1, 11, 0))
                .createdBy("userTest2")
                .build();

        var pageSize = 20;
        var pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        // when
        var result = sut.comments(condition, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @DisplayName("[성공][service] 전체 게시글별 댓글 리스트 조회(group by articleId)")
    @Test
    void givenNothing_whenCommentsGroupByArticleId_thenWorksFine() {
        // given

        // when
        var result = sut.commentsGroupByArticleId();

        // then
        assertThat(result.size()).isEqualTo(30);
        assertThat(result.get(2L).size()).isEqualTo(3);
    }
}
