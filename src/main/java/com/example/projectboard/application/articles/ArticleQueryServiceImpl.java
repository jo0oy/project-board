package com.example.projectboard.application.articles;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;
import com.example.projectboard.domain.articles.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArticleQueryServiceImpl implements ArticleQueryService {
    private final ArticleRepository articleRepository;

    @Override
    public ArticleInfo getArticle(Long articleId) {
        log.info("{}:{}", getClass().getSimpleName(), "getArticle(Long)");

        var article = articleRepository.findById(articleId)
                .orElseThrow(EntityNotFoundException::new);

        return new ArticleInfo(article);
    }

    @Override
    public Page<ArticleInfo> articles(ArticleCommand.SearchCondition condition, Pageable pageable) {
        log.info("{}:{}", getClass().getSimpleName(), "articles(ArticleCommand.SearchCondition, Pageable)");

        return articleRepository.findAll(condition.toSearchCondition(), getPageRequest(pageable))
                .map(ArticleInfo::new);
    }

    @Override
    public List<ArticleInfo> articleList(ArticleCommand.SearchCondition condition) {
        log.info("{}:{}", getClass().getSimpleName(), "articleList(ArticleCommand.SearchCondition)");

        return articleRepository.findAll(condition.toSearchCondition())
                .stream()
                .map(ArticleInfo::new)
                .collect(Collectors.toList());
    }

    private PageRequest getPageRequest(Pageable pageable) {
        int page = (pageable.getPageNumber() <= 0) ? 0 : pageable.getPageNumber() - 1;
        return PageRequest.of(page, pageable.getPageSize(), pageable.getSort());
    }
}
