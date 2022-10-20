package com.example.projectboard.application.articles;

import com.example.projectboard.application.hashtags.HashtagQueryService;
import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.InvalidContentException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import com.example.projectboard.common.util.HashtagContentUtil;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.domain.articles.Article;
import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;
import com.example.projectboard.domain.articles.ArticleRepository;
import com.example.projectboard.domain.articles.articlehashtags.ArticleHashtag;
import com.example.projectboard.domain.hashtags.Hashtag;
import com.example.projectboard.domain.articles.articlehashtags.ArticleHashtagRepository;
import com.example.projectboard.domain.hashtags.HashtagRepository;
import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleCommandServiceImpl implements ArticleCommandService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagQueryService hashtagQueryService;
    private final HashtagRepository hashtagRepository;
    private final ArticleHashtagRepository articleHashtagRepository;

    /**
     * 새로운 Article 등록 메서드.
     *
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String)
     * @param command           : ArticleComment 등록을 위한 데이터가 담긴 객체 (ArticleCommand.RegisterReq)
     * @return ArticleInfo.MainInfo : 등록된 Article 엔티티를 ArticleCommentInfo.MainInfo 감싸서 반환.
     */
    @Override
    @Transactional
    public ArticleInfo.MainInfo registerArticle(String principalUsername, ArticleCommand.RegisterReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "registerArticle(String, ArticleCommand.RegisterReq)");

        // UserAccount 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(principalUsername).orElseThrow(() ->
                new UsernameNotFoundException("유저를 찾을 수 없습니다. username=" + principalUsername));

        // Article 엔티티 생성
        var article = command.toEntity(userAccount.getId());

        // 전달받은 hashtagContent 검증
        validateHashtagContent(command.getHashtagNames());

        // 저장할 hashtagSet 생성
        var hashtags = renewHashtagsFromList(command.getHashtagNames(), article);

        return new ArticleInfo.MainInfo(articleRepository.save(article), hashtags);
    }

    /**
     * 새로운 Article 등록 메서드. -> Controller 에서 hashtagContent 검증 완료 -> 검증 로직 불필요!
     *
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String)
     * @param command           : ArticleComment 등록을 위한 데이터가 담긴 객체 (ArticleCommand.RegisterReq)
     * @return ArticleInfo.MainInfo : 등록된 Article 엔티티를 ArticleCommentInfo.MainInfo 감싸서 반환.
     */
    @Override
    @Transactional
    public ArticleInfo.MainInfo registerArticleWithValidHashtags(String principalUsername, ArticleCommand.RegisterReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "registerArticle(String, ArticleCommand.RegisterReq)");

        // UserAccount 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(principalUsername).orElseThrow(() ->
                new UsernameNotFoundException("유저를 찾을 수 없습니다. username=" + principalUsername));

        // Article 엔티티 생성
        var article = command.toEntity(userAccount.getId());

        // 저장할 hashtagSet 생성
        var hashtags = renewHashtagsFromList(command.getHashtagNames(), article);

        return new ArticleInfo.MainInfo(articleRepository.save(article), hashtags);
    }

    /**
     * Article 내용 수정 메서드.
     *
     * @param articleId         : 수정하려는 Article id (Long)
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String) -> 수정 요청 권한 확인을 위해
     * @param command           : Article 수정을 위한 데이터가 담긴 객체 (ArticleCommand.UpdateReq)
     */
    @Override
    @Transactional
    public void update(Long articleId, String principalUsername, ArticleCommand.UpdateReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "update(Long, String, ArticleCommand.UpdateReq)");

        // 기존 ArticleHashtag 모두 삭제
        articleHashtagRepository.bulkDeleteByArticle_Id(articleId);

        // update 할 Article 엔티티 조회 후 모든 해시태그 삭제
        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        article.clearHashtags();

        // 수정 권한 확인하기 -> 본인 게시글인 경우 or 관리자 계정인 경우 수정 가능
        validateAuthorityToCommand(article.getUserId(), principalUsername);

        // 수정 권한 확인 -> 수정 진행
        article.update(command.getTitle(), command.getContent());

        // 업데이트할 해시태그 목록 생성!
        renewHashtagsFromList(command.getHashtagNames(), article);
    }

    /**
     * Article 삭제 메서드
     *
     * @param articleId         : 삭제하려는 Article id (Long)
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서의 username (String) -> 삭제 요청 권한 확인을 위해
     */
    @Override
    @Transactional
    public void delete(Long articleId, String principalUsername) {
        log.info("{}:{}", getClass().getSimpleName(), "delete(Long, String)");

        // 삭제할 Article 엔티티 조회
        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        // 삭제 권한 확인하기 -> 본인 게시글인 경우 or 관리자 계정인 경우 삭제 가능
        validateAuthorityToCommand(article.getUserId(), principalUsername);

        // 게시글 댓글 리스트 bulk delete
        articleCommentRepository.deleteByArticleId(articleId);

        // 게시글에 연결된 ArticleHashtag 리스트 삭제: bulk delete
        articleHashtagRepository.bulkDeleteByArticle_Id(articleId);

        // 게시글 삭제 진행
        articleRepository.delete(article);
    }

    private void validateAuthorityToCommand(Long articleUserId, String principalUsername) {
        log.info("수정/삭제 권한 확인 로직 실행");
        // 조회한 Article 엔티티의 연관 관계에 있는 UserAccount 엔티티 조회
        var writerUserAccount = userAccountRepository.findById(articleUserId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다. 요청 진행이 불가합니다."));

        // 수정 or 삭제 권한 확인하기 위한 UserAccount 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(principalUsername)
                .orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다. username=" + principalUsername));

        if (!writerUserAccount.equals(userAccount) && userAccount.getRole() != UserAccount.RoleType.ADMIN) {
            log.error("수정/삭제 권한이 없는 사용자입니다. username={}", principalUsername);
            throw new NoAuthorityToUpdateDeleteException();
        }
    }

    private void validateHashtagContent(List<String> hashtagNames) {
        if (HashtagContentUtil.verifyFormat(hashtagNames)) {
            log.debug("올바르지 않은 해시태그 내용 값입니다. Format 문제 발생.");
            throw new InvalidContentException("해시태그는 '#'로 시작하는 공백이 없는 문자열이어야 합니다. " +
                    "다수의 해시태그를 입력할 경우, 구분자는 ','와 '/'만 가능합니다.");
        }

        if (HashtagContentUtil.verifyHashtagSize(hashtagNames)) {
            log.debug("올바르지 않은 해시태그 내용 값입니다. Size 문제 발생.");
            throw new InvalidContentException("해시태그는 최대 10개까지 입력 가능합니다.");
        }
    }

    private Set<ArticleHashtag> renewHashtagsFromList(List<String> hashtagList, Article article) {
        log.info("저장할 ArticleHashtag Set 생성");
        var set = new LinkedHashSet<>(hashtagList);
        var existingHashtagSet = hashtagQueryService.hashtagListByHashtagNames(set);

        var hashtags = new LinkedHashSet<ArticleHashtag>();

        for (String actualHashtagName : set) {
            existingHashtagSet.stream()
                    .filter(h -> h.getHashtagName().equalsIgnoreCase(actualHashtagName))
                    .findFirst()
                    .ifPresentOrElse(h -> hashtags.add(ArticleHashtag.of(actualHashtagName, article, h)),
                            () -> hashtags.add(ArticleHashtag.of(
                                    actualHashtagName,
                                    article,
                                    hashtagRepository.save(Hashtag.of(actualHashtagName.toLowerCase()))))
                    );
        }

        return hashtags;
    }
}
