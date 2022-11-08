package com.example.projectboard.application.articles;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.domain.articlecomments.ArticleCommentRepository;
import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;
import com.example.projectboard.domain.articles.ArticleRepository;
import com.example.projectboard.domain.articles.articlehashtags.ArticleHashtag;
import com.example.projectboard.domain.articles.articlehashtags.ArticleHashtagRepository;
import com.example.projectboard.domain.likes.LikeRepository;
import com.example.projectboard.domain.users.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArticleQueryServiceImpl implements ArticleQueryService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleHashtagRepository articleHashtagRepository;
    private final LikeRepository likeRepository;
    private final UserAccountRepository userAccountRepository;

    /**
     * Article 단일 조회 메서드.
     * @param articleId : 조회하려는 Article id (Long)
     * @return ArticleInfo.MainInfo : 조회한 Article -> ArticleInfo.MainInfo 감싸서 반환.
     */
    @Override
    public ArticleInfo.MainInfo getArticle(Long articleId) {
        log.info("{}:{}", getClass().getSimpleName(), "getArticle(Long)");

        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> {throw new EntityNotFoundException("존재하지 않는 게시글입니다.");});

        return new ArticleInfo.MainInfo(article);
    }

    /**
     * 해당 게시글의 댓글 리스트 포함한 Article 단일 조회 메서드.
     * @param articleId : 조회하려는 Article id (Long)
     * @return ArticleInfo.ArticleWithCommentsInfo : 조회한 Article, List<ArticleCommentInfo.SimpleInfo>> -> ArticleInfo.ArticleWithCommentsInfo 감싸서 반환.
     */
    @Override
    public ArticleInfo.ArticleWithCommentsInfo getArticleWithComments(Long articleId, String principalUsername) {
        log.info("{}:{}", getClass().getSimpleName(), "getArticleWithComments(Long, String)");

        // 게시글 엔티티 조회
        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> {throw new EntityNotFoundException("존재하지 않는 게시글입니다.");});

        // 게시글 댓글 리스트 조회
        var commentList = articleCommentRepository.findByArticleId(articleId).stream()
                .map(ArticleCommentInfo.SimpleInfo::new)
                .collect(Collectors.toList());

        // 로그인한 사용자가 '좋아요'한 게시글인지 체크
        var likedArticle = false; // null 이거나 empty 문자열인 경우 -> false
        if(StringUtils.hasText(principalUsername))
            likedArticle = likeRepository.existsByArticle_IdAndUserAccount_Username(articleId, principalUsername);

        return new ArticleInfo.ArticleWithCommentsInfo(article, commentList, likedArticle);
    }

    /**
     * 검색 조건에 따른 Article 리스트 페이징 조회 메서드.
     * @param condition : Article 검색조건 (ArticleCommand.SearchCondition)
     * @param pageable : 페이징을 위한 페이징 정보 (Pageable)
     * @return Page<ArticleInfo.MainInfo> : 조회된 Article -> ArticleCommentInfo.MainInfo 변환해서 반환.
     */
    @Override
    public Page<ArticleInfo.MainInfo> articles(ArticleCommand.SearchCondition condition, Pageable pageable) {
        log.info("{}:{}", getClass().getSimpleName(), "articles(ArticleCommand.SearchCondition, Pageable)");

        // 해시태그 검색 조회 하는 경우
        if (StringUtils.hasText(condition.getHashtag())) {
            return articleHashtagRepository.findByHashtagNameIgnoreCase(condition.getHashtag(), getPageRequest(pageable))
                    .map(ArticleHashtag::getArticle)
                    .map(ArticleInfo.MainInfo::new);
        }

        return articleRepository.findAll(condition.toSearchCondition(), getPageRequest(pageable))
                .map(ArticleInfo.MainInfo::new);
    }

    /**
     * 검색 조건에 따른 Article 리스트 조회 메서드.
     * @param condition : Article 검색조건 (ArticleCommand.SearchCondition)
     * @return List<ArticleInfo.MainInfo> : 조회된 Article -> ArticleCommentInfo.MainInfo 변환해서 반환.
     */
    @Override
    public List<ArticleInfo.MainInfo> articleList(ArticleCommand.SearchCondition condition) {
        log.info("{}:{}", getClass().getSimpleName(), "articleList(ArticleCommand.SearchCondition)");

        // 해시태그 검색 조회 하는 경우
        if (StringUtils.hasText(condition.getHashtag())) {
            return articleHashtagRepository.findByHashtagNameIgnoreCase(condition.getHashtag())
                    .stream()
                    .map(ArticleHashtag::getArticle)
                    .map(ArticleInfo.MainInfo::new)
                    .collect(Collectors.toList());
        }

        return articleRepository.findAll(condition.toSearchCondition())
                .stream()
                .map(ArticleInfo.MainInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ArticleInfo.MainInfo> articlesWrittenBy(String principalUsername, ArticleCommand.SearchCondition condition, Pageable pageable) {
        log.info("{}:{}", getClass().getSimpleName(), "articlesWrittenBy(String, ArticleCommand.SearchCondition, Pageable)");

        // 인증된 사용자 엔티티(UserAccount) 조회
        var userAccount = userAccountRepository.findByUsername(principalUsername)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. username=" + principalUsername));

        return articleRepository
                .findAllByUserId(userAccount.getId(), condition.toSearchCondition(), getPageRequest(pageable))
                .map(ArticleInfo.MainInfo::new);
    }

    @Override
    public Page<ArticleInfo.MainInfo> articlesByHashtagId(Long hashtagId, Pageable pageable) {
        return articleHashtagRepository.findByHashtagId(hashtagId, getPageRequest(pageable))
                .map(ArticleHashtag::getArticle)
                .map(ArticleInfo.MainInfo::new);
    }

    @Override
    public Page<ArticleInfo.MainInfo> articlesByHashtagName(String hashtagName, Pageable pageable) {
        return articleHashtagRepository.findByHashtagNameIgnoreCase(hashtagName, getPageRequest(pageable))
                .map(ArticleHashtag::getArticle)
                .map(ArticleInfo.MainInfo::new);
    }

    /**
     * 모든 게시글 수 반환하는 메서드.
     * @return long : 모든 게시글 수
     */
    @Override
    public long articleCount() {
        return articleRepository.count();
    }

    private PageRequest getPageRequest(Pageable pageable) {

        if(pageable.getPageNumber() < 0) {
            log.error("IllegalArgumentException. Invalid PageNumber!!");
            throw new IllegalArgumentException("페이지 번호는 0보다 작을 수 없습니다. 올바른 페이지 번호를 입력하세요.");
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    }
}
