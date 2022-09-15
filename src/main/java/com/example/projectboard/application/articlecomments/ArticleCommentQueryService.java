package com.example.projectboard.application.articlecomments;

import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ArticleCommentQueryService {

    // 게시물 댓글 단건 조회 by commentId
    ArticleCommentInfo.MainInfo getComment(Long commentId);

    // 해당 게시글의 댓글 리스트 조회 by articleId
    Page<ArticleCommentInfo.SimpleInfo> commentsByArticleId(Long articleId, Pageable pageable);

    // 전체 댓글 조회 (검색조건, 페이징)
    Page<ArticleCommentInfo.MainInfo> comments(ArticleCommentCommand.SearchCondition condition, Pageable pageable);

    // 전체 댓글 조회 (검색조건)
    List<ArticleCommentInfo.MainInfo> comments(ArticleCommentCommand.SearchCondition condition);

    // 게시글별 게시글 댓글 조회 (group by)
    Map<Long, List<ArticleCommentInfo.MainInfo>> commentsGroupByArticleId();
}
