package com.example.projectboard.application.likes;

import com.example.projectboard.domain.articles.ArticleInfo;
import com.example.projectboard.domain.likes.ArticleLikeCommand;
import com.example.projectboard.domain.likes.Like;
import com.example.projectboard.domain.likes.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArticleLikeQueryServiceImpl implements ArticleLikeQueryService {

    private final LikeRepository likeRepository;

    /**
     * 인증된 사용자가 '좋아요' 누른 게시글 리스트 검색/페이징/정렬 조회 메서드.
     * @param principalUsername : 인증 객체에 담긴 사용자의 아이디(username)
     * @param condition : '좋아요' 누른 게시글 리스트 중 검색 조건
     * @param pageable : 페이징, 정렬을 위한 PageRequest 정보
     * @return Page<ArticleInfo.MainInfo>
     */
    @Override
    public Page<ArticleInfo.MainInfo> articlesLiked(String principalUsername,
                                                    ArticleLikeCommand.SearchCondition condition,
                                                    Pageable pageable) {
        log.info("{}:{}", getClass().getSimpleName(), "articlesLiked(String, ArticleLikeCommand.SearchCondition)");

        return likeRepository
                .articleLikesByUsername(principalUsername, condition.toSearchCondition(), getPageRequest(pageable))
                .map(Like::getArticle)
                .map(ArticleInfo.MainInfo::new);
    }

    private PageRequest getPageRequest(Pageable pageable) {

        if(pageable.getPageNumber() < 0) {
            log.error("IllegalArgumentException. Invalid PageNumber!!");
            throw new IllegalArgumentException("페이지 번호는 0보다 작을 수 없습니다. 올바른 페이지 번호를 입력하세요.");
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    }
}
