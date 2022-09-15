package com.example.projectboard.application.articles;

import com.example.projectboard.common.exception.EntityNotFoundException;
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

    @Override
    @Transactional
    public ArticleInfo registerArticle(ArticleCommand.RegisterReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "registerArticle(ArticleCommand.RegisterReq)");

        return new ArticleInfo(articleRepository.save(command.toEntity()));
    }

    @Override
    @Transactional
    public void update(Long articleId, ArticleCommand.UpdateReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "update(Long, ArticleCommand.UpdateReq)");

        // update 할 엔티티 조회
        var article =
                articleRepository.findById(articleId).orElseThrow(EntityNotFoundException::new);

        article.update(command.getTitle(), command.getContent(), command.getHashtag());
    }
}
