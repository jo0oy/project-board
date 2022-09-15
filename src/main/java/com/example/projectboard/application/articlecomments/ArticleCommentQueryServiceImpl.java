package com.example.projectboard.application.articlecomments;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Override
    public ArticleCommentInfo.MainInfo getComment(Long commentId) {
        log.info("{}:{}", getClass().getSimpleName(), "getComment(Long)");

        return articleCommentRepository.findWithArticleId(commentId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Page<ArticleCommentInfo.SimpleInfo> commentsByArticleId(Long articleId,
                                                                   Pageable pageable) {
        log.info("{}:{}", getClass().getSimpleName(), "commentsByArticleId(Long, Pageable)");

        return articleCommentRepository.findByArticleId(articleId, getPageRequest(pageable))
                .map(ArticleCommentInfo.SimpleInfo::new);
    }

    @Override
    public Page<ArticleCommentInfo.MainInfo> comments(ArticleCommentCommand.SearchCondition condition,
                                                      Pageable pageable) {
        log.info("{}:{}", getClass().getSimpleName(), "comments(ArticleCommentCommand.SearchCondition, Pageable)");

        return articleCommentRepository.findAllWithArticleId(condition.toSearchCondition(), getPageRequest(pageable));
    }

    @Override
    public List<ArticleCommentInfo.MainInfo> comments(ArticleCommentCommand.SearchCondition condition) {
        log.info("{}:{}", getClass().getSimpleName(), "comments(ArticleCommentCommand.SearchCondition)");

        return articleCommentRepository.findAllWithArticleId(condition.toSearchCondition());
    }

    @Override
    public Map<Long, List<ArticleCommentInfo.MainInfo>> commentsGroupByArticleId() {
        log.info("{}:{}", getClass().getSimpleName(), "commentsGroupByArticleId()");

        return articleCommentRepository.findAllWithArticleId().stream()
                .collect(Collectors.groupingBy(ArticleCommentInfo.MainInfo::getArticleId));
    }

    private PageRequest getPageRequest(Pageable pageable) {
        int page = (pageable.getPageNumber() <= 0) ? 0 : pageable.getPageNumber() - 1;
        return PageRequest.of(page, pageable.getPageSize(), pageable.getSort());
    }
}
