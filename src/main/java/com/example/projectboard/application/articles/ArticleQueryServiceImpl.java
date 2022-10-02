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
    public ArticleInfo.MainInfo getArticle(Long articleId) {
        log.info("{}:{}", getClass().getSimpleName(), "getArticle(Long)");

        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> {throw new EntityNotFoundException("존재하지 않는 게시글입니다.");});

        return new ArticleInfo.MainInfo(article);
    }

    @Override
    public Page<ArticleInfo.MainInfo> articles(ArticleCommand.SearchCondition condition, Pageable pageable) {
        log.info("{}:{}", getClass().getSimpleName(), "articles(ArticleCommand.SearchCondition, Pageable)");

        return articleRepository.findAll(condition.toSearchCondition(), getPageRequest(pageable))
                .map(ArticleInfo.MainInfo::new);
    }

    @Override
    public List<ArticleInfo.MainInfo> articleList(ArticleCommand.SearchCondition condition) {
        log.info("{}:{}", getClass().getSimpleName(), "articleList(ArticleCommand.SearchCondition)");

        return articleRepository.findAll(condition.toSearchCondition())
                .stream()
                .map(ArticleInfo.MainInfo::new)
                .collect(Collectors.toList());
    }

    private PageRequest getPageRequest(Pageable pageable) {

        if(pageable.getPageNumber() < 0) {
            log.error("IllegalArgumentException. Invalid PageNumber!!");
            throw new IllegalArgumentException("페이지 번호는 0보다 작을 수 없습니다. 올바른 페이지 번호를 입력하세요.");
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    }
}
