package com.example.projectboard.application.articles;

import com.example.projectboard.application.hashtags.HashtagQueryService;
import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.InvalidContentException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import com.example.projectboard.common.util.HashtagContentUtils;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    private static final String VIEW_COUNT_COOKIE_NAME = "articleViewed";

    /**
     * ????????? Article ?????? ?????????.
     *
     * @param principalUsername : ?????? ?????? ??????(Security Context)??? ?????? ?????? ????????? ??????????????? username (String)
     * @param command           : ArticleComment ????????? ?????? ???????????? ?????? ?????? (ArticleCommand.RegisterReq)
     * @return ArticleInfo.MainInfo : ????????? Article ???????????? ArticleCommentInfo.MainInfo ????????? ??????.
     */
    @Override
    @Transactional
    public ArticleInfo.MainInfo registerArticle(String principalUsername, ArticleCommand.RegisterReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "registerArticle(String, ArticleCommand.RegisterReq)");

        // UserAccount ????????? ??????
        var userAccount = userAccountRepository.findByUsername(principalUsername).orElseThrow(() ->
                new UsernameNotFoundException("????????? ?????? ??? ????????????. username=" + principalUsername));

        // Article ????????? ??????
        var article = command.toEntity(userAccount.getId());

        // ???????????? hashtagContent ??????
        validateHashtagContent(command.getHashtagNames());

        // ????????? hashtagSet ??????
        var hashtags = renewHashtagsFromList(command.getHashtagNames(), article);

        return new ArticleInfo.MainInfo(articleRepository.save(article), hashtags);
    }

    /**
     * ????????? Article ?????? ?????????. -> Controller ?????? hashtagContent ?????? ?????? -> ?????? ?????? ?????????!
     *
     * @param principalUsername : ?????? ?????? ??????(Security Context)??? ?????? ?????? ????????? ??????????????? username (String)
     * @param command           : ArticleComment ????????? ?????? ???????????? ?????? ?????? (ArticleCommand.RegisterReq)
     * @return ArticleInfo.MainInfo : ????????? Article ???????????? ArticleCommentInfo.MainInfo ????????? ??????.
     */
    @Override
    @Transactional
    public ArticleInfo.MainInfo registerArticleWithValidHashtags(String principalUsername, ArticleCommand.RegisterReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "registerArticle(String, ArticleCommand.RegisterReq)");

        // UserAccount ????????? ??????
        var userAccount = userAccountRepository.findByUsername(principalUsername).orElseThrow(() ->
                new UsernameNotFoundException("????????? ?????? ??? ????????????. username=" + principalUsername));

        // Article ????????? ??????
        var article = command.toEntity(userAccount.getId());

        // ????????? hashtagSet ??????
        var hashtags = renewHashtagsFromList(command.getHashtagNames(), article);

        return new ArticleInfo.MainInfo(articleRepository.save(article), hashtags);
    }

    /**
     * ????????? ????????? ?????? ?????????
     * @param articleId : ????????? ???????????? ????????? Id
     * @param request : ???????????? HttpServletRequest
     * @param response : ????????? HttpServletResponse
     */
    @Override
    @Transactional
    public void viewCountUp(Long articleId, HttpServletRequest request, HttpServletResponse response) {
        log.info("{}:{}", getClass().getSimpleName(), "viewCountUp(Long, HttpServletRequest, HttpServletResponse)");

        Cookie viewCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(VIEW_COUNT_COOKIE_NAME)) {
                    viewCookie = cookie;
                }
            }
        }

        if (viewCookie != null) {
            if (!viewCookie.getValue().contains("[" + articleId.toString() + "]")) {
                articleRepository.updateViewCount(articleId);
                viewCookie.setValue(viewCookie.getValue() + "_[" + articleId + "]");
                viewCookie.setMaxAge(60 * 60 * 12);
                viewCookie.setHttpOnly(true);
                response.addCookie(viewCookie);
            }
        } else {
            articleRepository.updateViewCount(articleId);
            Cookie newCookie = new Cookie(VIEW_COUNT_COOKIE_NAME,"[" + articleId + "]");
            newCookie.setMaxAge(60 * 60 * 12); // 12??????
            newCookie.setComment("????????? ?????? ?????? ?????? ??????"); // ?????? ?????? ?????? ??????
            newCookie.setHttpOnly(true);
            response.addCookie(newCookie);
        }
    }

    /**
     * Article ?????? ?????? ?????????.
     *
     * @param articleId         : ??????????????? Article id (Long)
     * @param principalUsername : ?????? ?????? ??????(Security Context)??? ?????? ?????? ????????? ??????????????? username (String) -> ?????? ?????? ?????? ????????? ??????
     * @param command           : Article ????????? ?????? ???????????? ?????? ?????? (ArticleCommand.UpdateReq)
     */
    @Override
    @Transactional
    public void update(Long articleId, String principalUsername, ArticleCommand.UpdateReq command) {
        log.info("{}:{}", getClass().getSimpleName(), "update(Long, String, ArticleCommand.UpdateReq)");

        // ?????? ArticleHashtag ?????? ??????
        articleHashtagRepository.bulkDeleteByArticle_Id(articleId);

        // update ??? Article ????????? ?????? ??? ?????? ???????????? ??????
        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("???????????? ?????? ??????????????????."));

        article.clearHashtags();

        // ?????? ?????? ???????????? -> ?????? ???????????? ?????? or ????????? ????????? ?????? ?????? ??????
        validateAuthorityToCommand(article.getUserId(), principalUsername);

        // ?????? ?????? ?????? -> ?????? ??????
        article.update(command.getTitle(), command.getContent());

        // ??????????????? ???????????? ?????? ??????!
        renewHashtagsFromList(command.getHashtagNames(), article);
    }

    /**
     * Article ?????? ?????????
     *
     * @param articleId         : ??????????????? Article id (Long)
     * @param principalUsername : ?????? ?????? ??????(Security Context)??? ?????? ?????? ????????? ??????????????? username (String) -> ?????? ?????? ?????? ????????? ??????
     */
    @Override
    @Transactional
    public void delete(Long articleId, String principalUsername) {
        log.info("{}:{}", getClass().getSimpleName(), "delete(Long, String)");

        // ????????? Article ????????? ??????
        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("???????????? ?????? ??????????????????."));

        // ?????? ?????? ???????????? -> ?????? ???????????? ?????? or ????????? ????????? ?????? ?????? ??????
        validateAuthorityToCommand(article.getUserId(), principalUsername);

        // ????????? ?????? ????????? bulk delete
        articleCommentRepository.deleteByArticleId(articleId);

        // ???????????? ????????? ArticleHashtag ????????? ??????: bulk delete
        articleHashtagRepository.bulkDeleteByArticle_Id(articleId);

        // ????????? ?????? ??????
        articleRepository.delete(article);
    }

    private void validateAuthorityToCommand(Long articleUserId, String principalUsername) {
        log.info("??????/?????? ?????? ?????? ?????? ??????");
        // ????????? Article ???????????? ?????? ????????? ?????? UserAccount ????????? ??????
        var writerUserAccount = userAccountRepository.findById(articleUserId)
                .orElseThrow(() -> new EntityNotFoundException("???????????? ?????? ???????????????. ?????? ????????? ???????????????."));

        // ?????? or ?????? ?????? ???????????? ?????? UserAccount ????????? ??????
        var userAccount = userAccountRepository.findByUsername(principalUsername)
                .orElseThrow(() -> new UsernameNotFoundException("????????? ???????????? ????????????. username=" + principalUsername));

        if (!writerUserAccount.equals(userAccount) && userAccount.getRole() != UserAccount.RoleType.ADMIN) {
            log.error("??????/?????? ????????? ?????? ??????????????????. username={}", principalUsername);
            throw new NoAuthorityToUpdateDeleteException();
        }
    }

    private void validateHashtagContent(List<String> hashtagNames) {
        if (HashtagContentUtils.verifyFormat(hashtagNames)) {
            log.debug("???????????? ?????? ???????????? ?????? ????????????. Format ?????? ??????.");
            throw new InvalidContentException("??????????????? '#'??? ???????????? ????????? ?????? ?????????????????? ?????????. " +
                    "????????? ??????????????? ????????? ??????, ???????????? ','??? '/'??? ???????????????.");
        }

        if (HashtagContentUtils.verifyHashtagSize(hashtagNames)) {
            log.debug("???????????? ?????? ???????????? ?????? ????????????. Size ?????? ??????.");
            throw new InvalidContentException("??????????????? ?????? 10????????? ?????? ???????????????.");
        }
    }

    private Set<ArticleHashtag> renewHashtagsFromList(List<String> hashtagList, Article article) {
        log.info("????????? ArticleHashtag Set ??????");
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
