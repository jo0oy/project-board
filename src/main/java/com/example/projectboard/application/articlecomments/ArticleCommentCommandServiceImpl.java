package com.example.projectboard.application.articlecomments;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import com.example.projectboard.domain.articlecomments.ArticleComment;
import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.domain.articles.ArticleRepository;
import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountCacheRepository;
import com.example.projectboard.domain.users.UserAccountInfoMapper;
import com.example.projectboard.domain.users.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleCommentCommandServiceImpl implements ArticleCommentCommandService {

    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountCacheRepository userAccountCacheRepository;
    private final UserAccountInfoMapper userAccountInfoMapper;

    /**
     * 새로운 ArticleComment 등록 메서드.
     * @param writer : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 writer (String)
     * @param command : ArticleComment 등록을 위한 데이터가 담긴 객체 (ArticleCommentCommand.RegisterReq)
     * @return ArticleCommentInfo.MainInfo : 등록된 ArticleComment 엔티티를 ArticleCommentInfo.MainInfo에 감싸서 반환.
     */
    @Override
    @Transactional
    public ArticleCommentInfo.MainInfo registerComment(String writer, ArticleCommentCommand.RegisterReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "registerComment(String, ArticleCommentCommand.RegisterReq)");

        // 게시글(Article) 엔티티 조회
        var article = articleRepository.findById(command.getArticleId())
                .orElseThrow(EntityNotFoundException::new);

        // 작성자(UserAccount) 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(writer).orElseThrow(() ->
                new UsernameNotFoundException("유저를 찾을 수 없습니다. username=" + writer));

        // 부모 댓글 존재 여부 확인 후, ArticleComment 엔티티 변경
        ArticleComment comment;
        if (Objects.nonNull(command.getParentId())) { // 부모 댓글 존재함
            var parent = articleCommentRepository.getReferenceById(command.getParentId());
            comment = command.toEntity(article, userAccount.getId(), parent);
        } else {
            comment = command.toEntity(article, userAccount.getId());
        }

        // 댓글 등록
        var savedComment = articleCommentRepository.save(comment);

        return new ArticleCommentInfo.MainInfo(savedComment, command.getArticleId(), command.getParentId());
    }

    /**
     * ArticleComment 내용 수정 메서드.
     * @param commentId : 수정하려는 ArticleComment 의 id (Long)
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String) -> 수정 요청 권한 확인을 위해
     * @param command : ArticleComment 수정을 위한 데이터가 담긴 객체 (ArticleCommentCommand.UpdateReq)
     */
    @Override
    @Transactional
    public void update(Long commentId, String principalUsername, ArticleCommentCommand.UpdateReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "update(Long, String, ArticleCommentCommand.UpdateReq)");

        // 업데이트할 ArticleComment 엔티티 조회
        var comment = articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        // 수정 권한 확인
        if (validateAuthorityToCommand(comment.getUserId(), principalUsername)) {
            // 댓글 수정
            comment.update(command.getCommentBody());
        } else {
            log.error("수정 권한이 없는 사용자입니다. username={}", principalUsername);
            throw new NoAuthorityToUpdateDeleteException();
        }
    }

    /**
     * ArticleComment 삭제 메서드
     * @param commentId : 삭제하려는 ArticleComment id (Long)
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String) -> 삭제 요청 권한 확인을 위해
     */
    @Override
    @Transactional
    public void delete(Long commentId, String principalUsername) {
        log.info("{}:{}", getClass().getSimpleName(), "delete(Long, String)");

        // ArticleComment 엔티티 조회
        var comment = articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        // 삭제 권한 확인
        if (validateAuthorityToCommand(comment.getUserId(), principalUsername)) {

            if (comment.hasChilds()) { // 자식 댓글 존재 여부 확인 후, 자식 댓글을 먼저 모두 삭제한다.
                articleCommentRepository.bulkDeleteByParentId(commentId);
            }

            // 댓글 삭제
            articleCommentRepository.deleteById(commentId);

        } else {
            log.error("삭제 권한이 없는 사용자입니다. username={}", principalUsername);
            throw new NoAuthorityToUpdateDeleteException();
        }
    }

    private boolean validateAuthorityToCommand(Long commentUserId, String principalUsername) {
        // 댓글 작성자 UserAccount 엔티티 조회
        var writer = userAccountRepository.findById(commentUserId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다. 요청 진행이 불가합니다."));

        // 수정 or 삭제 권한 확인하기 위한 UserAccount 엔티티 조회
        var userAccount = userAccountCacheRepository.get(principalUsername)
                .orElseGet(() ->
                        userAccountInfoMapper.toCacheDto(
                                userAccountRepository.findByUsername(principalUsername)
                                        .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다. username=" + principalUsername)))
                );

        return writer.validateAuthority(userAccount.getId(), userAccount.getUsername())
                || userAccount.getRole() == UserAccount.RoleType.ADMIN;
    }
}
