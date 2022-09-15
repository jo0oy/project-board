package com.example.projectboard.application.articlecomments;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.domain.articles.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleCommentCommandServiceImpl implements ArticleCommentCommandService {

    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;

    @Override
    @Transactional
    public ArticleCommentInfo.MainInfo registerComment(ArticleCommentCommand.RegisterReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "registerComment");

        // 게시글 엔티티 조회
        var article = articleRepository.findById(command.getArticleId())
                .orElseThrow(EntityNotFoundException::new);

        // 댓글 등록
        var savedComment = articleCommentRepository.save(command.toEntity(article));
        return new ArticleCommentInfo.MainInfo(savedComment, command.getArticleId());
    }

    @Override
    @Transactional
    public void update(Long commentId, ArticleCommentCommand.UpdateReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "updateComment");

        // 업데이트할 엔티티 조회
        var comment = articleCommentRepository.findById(commentId)
                .orElseThrow(EntityNotFoundException::new);

        // 댓글 수정
        comment.update(command.getContent());
    }
}
