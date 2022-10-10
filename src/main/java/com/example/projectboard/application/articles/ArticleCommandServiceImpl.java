package com.example.projectboard.application.articles;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;
import com.example.projectboard.domain.articles.ArticleRepository;
import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountRepository;
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
    private final UserAccountRepository userAccountRepository;

    /**
     * 새로운 Article 등록 메서드.
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String)
     * @param command : ArticleComment 등록을 위한 데이터가 담긴 객체 (ArticleCommand.RegisterReq)
     * @return ArticleInfo.MainInfo : 등록된 Article 엔티티를 ArticleCommentInfo.MainInfo 감싸서 반환.
     */
    @Override
    @Transactional
    public ArticleInfo.MainInfo registerArticle(String principalUsername, ArticleCommand.RegisterReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "registerArticle(String, ArticleCommand.RegisterReq)");

        // UserAccount 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(principalUsername).orElseThrow(() ->
                new UsernameNotFoundException("유저를 찾을 수 없습니다. username=" + principalUsername));

        return new ArticleInfo.MainInfo(articleRepository.save(command.toEntity(userAccount.getId())));
    }

    /**
     * Article 내용 수정 메서드.
     * @param articleId : 수정하려는 Article id (Long)
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String) -> 수정 요청 권한 확인을 위해
     * @param command : Article 수정을 위한 데이터가 담긴 객체 (ArticleCommand.UpdateReq)
     */
    @Override
    @Transactional
    public void update(Long articleId, String principalUsername, ArticleCommand.UpdateReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "update(Long, String, ArticleCommand.UpdateReq)");

        // update 할 Article 엔티티 조회
        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        // 수정 권한 확인하기 -> 본인 게시글인 경우 or 관리자 계정인 경우 수정 가능
        if (validateAuthorityToCommand(article.getUserId(), principalUsername)) {
            // 수정 권한 확인 -> 수정 진행
            article.update(command.getTitle(), command.getContent(), command.getHashtag());
        } else {
            log.error("수정 권한이 없는 사용자입니다. username={}", principalUsername);
            throw new NoAuthorityToUpdateDeleteException();
        }
    }

    /**
     * Article 삭제 메서드
     * @param articleId : 삭제하려는 Article id (Long)
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String) -> 삭제 요청 권한 확인을 위해
     */
    @Override
    @Transactional
    public void delete(Long articleId, String principalUsername) {
        log.info("{}:{}", getClass().getSimpleName(), "delete(Long, String)");

        // 게시글 댓글 리스트 bulk delete
        articleCommentRepository.deleteByArticleId(articleId);

        // 삭제할 Article 엔티티 조회
        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        // 삭제 권한 확인하기 -> 본인 게시글인 경우 or 관리자 계정인 경우 삭제 가능
        if (validateAuthorityToCommand(article.getUserId(), principalUsername)) {
            // 삭제 권한 확인 -> 삭제 진행
            articleRepository.delete(article);
        } else {
            log.error("삭제 권한이 없는 사용자입니다. username={}", principalUsername);
            throw new NoAuthorityToUpdateDeleteException();
        }
    }

    private boolean validateAuthorityToCommand(Long articleUserId, String principalUsername) {
        // 조회한 Article 엔티티의 연관 관계에 있는 UserAccount 엔티티 조회
        var writerUserAccount = userAccountRepository.findById(articleUserId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다. 요청 진행이 불가합니다."));

        // 수정 or 삭제 권한 확인하기 위한 UserAccount 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(principalUsername)
                .orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다. username=" + principalUsername));

        return writerUserAccount.equals(userAccount) || userAccount.getRole() == UserAccount.RoleType.ADMIN;
    }
}
