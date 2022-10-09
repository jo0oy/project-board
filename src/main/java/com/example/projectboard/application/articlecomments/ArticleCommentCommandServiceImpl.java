package com.example.projectboard.application.articlecomments;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.domain.articles.ArticleRepository;
import com.example.projectboard.domain.users.UserAccountRepository;
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
    private final UserAccountRepository userAccountRepository;

    /**
     * 새로운 ArticleComment 등록 메서드.
     * @param username : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String)
     * @param command : ArticleComment 등록을 위한 데이터가 담긴 객체 (ArticleCommentCommand.RegisterReq)
     * @return ArticleCommentInfo.MainInfo : 등록된 ArticleComment 엔티티를 ArticleCommentInfo.MainInfo에 감싸서 반환.
     */
    @Override
    @Transactional
    public ArticleCommentInfo.MainInfo registerComment(String username, ArticleCommentCommand.RegisterReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "registerComment(String, ArticleCommentCommand.RegisterReq)");

        // 게시글(Article) 엔티티 조회
        var article = articleRepository.findById(command.getArticleId())
                .orElseThrow(EntityNotFoundException::new);

        // 작성자(UserAccount) 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("유저를 찾을 수 없습니다. username=" + username));

        // 댓글 등록
        var savedComment = articleCommentRepository.save(command.toEntity(article, userAccount));
        return new ArticleCommentInfo.MainInfo(savedComment, command.getArticleId());
    }

    /**
     * ArticleComment 내용 수정 메서드.
     * @param commentId : 수정하려는 ArticleComment의 id (Long)
     * @param username : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String) -> 수정 요청 권한 확인을 위해
     * @param command : ArticleComment 수정을 위한 데이터가 담긴 객체 (ArticleCommentCommand.UpdateReq)
     */
    @Override
    @Transactional
    public void update(Long commentId, String username, ArticleCommentCommand.UpdateReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "update(Long, String, ArticleCommentCommand.UpdateReq)");

        // 업데이트할 ArticleComment 엔티티 조회
        var comment = articleCommentRepository.findByIdFetchJoinUserAccount(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        // 사용자 계정 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("유저를 찾을 수 없습니다. username=" + username));

        // 수정 권한 확인
        if (comment.getUserAccount().equals(userAccount)) {
            // 댓글 수정
            comment.update(command.getContent());
        } else {
            log.error("수정 권한이 없는 사용자입니다. username={}", username);
            throw new NoAuthorityToUpdateDeleteException();
        }
    }

    /**
     * ArticleComment 삭제 메서드
     * @param commentId : 삭제하려는 ArticleComment id (Long)
     * @param username : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String) -> 삭제 요청 권한 확인을 위해
     */
    @Override
    @Transactional
    public void delete(Long commentId, String username) {
        log.info("{}:{}", getClass().getSimpleName(), "delete(Long, String)");

        // ArticleComment 엔티티 조회
        var comment = articleCommentRepository.findByIdFetchJoinUserAccount(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        // 사용자 계정 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("유저를 찾을 수 없습니다. username=" + username));

        // 삭제 권한 확인
        if (comment.getUserAccount().equals(userAccount)) {
            // 댓글 삭제
            articleCommentRepository.delete(comment);
        } else {
            log.error("삭제 권한이 없는 사용자입니다. username={}", username);
            throw new NoAuthorityToUpdateDeleteException();
        }
    }
}
