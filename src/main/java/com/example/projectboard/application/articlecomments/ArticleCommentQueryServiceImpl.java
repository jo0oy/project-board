package com.example.projectboard.application.articlecomments;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.util.PageRequestUtils;
import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArticleCommentQueryServiceImpl implements ArticleCommentQueryService {
    private final ArticleCommentRepository articleCommentRepository;

    /**
     * ArticleComment 단일 조회 메서드.
     * @param commentId : 조회하려는 ArticleComment의 id (Long)
     * @return ArticleCommentInfo.MainInfo : 조회한 ArticleComment 객체 ArticleCommentInfo.MainInfo에 감싸서 반환.
     */
    @Override
    public ArticleCommentInfo.MainInfo getComment(Long commentId) {
        log.info("{}:{}", getClass().getSimpleName(), "getComment(Long)");

        return articleCommentRepository.findWithArticleId(commentId)
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * 특정 게시물에 달린 ArticleComment 리스트 페이징 조회 메서드.
     * @param articleId : 조회하려는 댓글 리스트의 게시물(Article) id (Long)
     * @param pageable : 페이징을 위한 페이징 정보 (Pageable)
     * @return Page<ArticleCommentInfo.SimpleInfo> : 댓글 리스트 페이징 조회 결과를 ArticleCommentInfo.SimpleInfo에 감싸서 반환.
     */
    @Override
    public Page<ArticleCommentInfo.SimpleInfo> commentsByArticleId(Long articleId,
                                                                   Pageable pageable) {
        log.info("{}:{}", getClass().getSimpleName(), "commentsByArticleId(Long, Pageable)");

        return articleCommentRepository.findByArticleId(articleId, PageRequestUtils.of(pageable))
                .map(ArticleCommentInfo.SimpleInfo::new);
    }

    /**
     * 검색 조건에 따른 ArticleComment 리스트 페이징 조회 메서드.
     * @param condition : ArticleComment 검색조건 (ArticleCommentCommand.SearchCondition)
     * @param pageable : 페이징을 위한 페이징 정보 (Pageable)
     * @return Page<ArticleCommentInfo.MainInfo> : 댓글 리스트 페이징 조회 결과를 ArticleCommentInfo.MainInfo에 감싸서 반환.
     */
    @Override
    public Page<ArticleCommentInfo.MainInfo> comments(ArticleCommentCommand.SearchCondition condition,
                                                      Pageable pageable) {
        log.info("{}:{}", getClass().getSimpleName(), "comments(ArticleCommentCommand.SearchCondition, Pageable)");

        return articleCommentRepository.findAllWithArticleId(condition.toSearchCondition(), PageRequestUtils.of(pageable));
    }

    /**
     * 검색 조건에 따른 ArticleComment 리스트 조회 메서드.
     * @param condition : ArticleComment 검색조건 (ArticleCommentCommand.SearchCondition)
     * @return List<ArticleCommentInfo.MainInfo> : 댓글 리스트 조회 결과를 ArticleCommentInfo.MainInfo에 감싸서 반환.
     */
    @Override
    public List<ArticleCommentInfo.MainInfo> comments(ArticleCommentCommand.SearchCondition condition) {
        log.info("{}:{}", getClass().getSimpleName(), "comments(ArticleCommentCommand.SearchCondition)");

        return articleCommentRepository.findAllWithArticleId(condition.toSearchCondition());
    }

    /**
     * ArticleComment 리스트를 게시글(Article) id 별로 grouping 조회하는 메서드.
     * @return Map<Long, List<ArticleCommentInfo.MainInfo>> : 전체 댓글 리스트를 게시글별로 grouping 한 Map 반환.
     */
    @Override
    public Map<Long, List<ArticleCommentInfo.MainInfo>> commentsGroupByArticleId() {
        log.info("{}:{}", getClass().getSimpleName(), "commentsGroupByArticleId()");

        return articleCommentRepository.findAllWithArticleId().stream()
                .collect(Collectors.groupingBy(ArticleCommentInfo.MainInfo::getArticleId));
    }
}
