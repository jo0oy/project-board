package com.example.projectboard.application.articles;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;
import com.example.projectboard.domain.articles.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleCommandServiceImpl implements ArticleCommandService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Override
    @Transactional
    public ArticleInfo.MainInfo registerArticle(ArticleCommand.RegisterReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "registerArticle(ArticleCommand.RegisterReq)");

        return new ArticleInfo.MainInfo(articleRepository.save(command.toEntity()));
    }

    @Override
    @Transactional
    public void update(Long articleId, ArticleCommand.UpdateReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "update(Long, ArticleCommand.UpdateReq)");

        // update 할 엔티티 조회
        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> {throw new EntityNotFoundException("존재하지 않는 게시글입니다.");});

        article.update(command.getTitle(), command.getContent(), command.getHashtag());
    }

    @Override
    @Transactional
    public void delete(Long articleId) {
        log.info("{}:{}", getClass().getSimpleName(), "delete(Long)");

        // 게시글 댓글 리스트 bulk delete
        articleCommentRepository.deleteByArticleId(articleId);

        // 게시글 엔티티 조회
        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> {throw new EntityNotFoundException("존재하지 않는 게시글입니다.");});

        // 게시글 삭제
        articleRepository.delete(article);
    }
}
