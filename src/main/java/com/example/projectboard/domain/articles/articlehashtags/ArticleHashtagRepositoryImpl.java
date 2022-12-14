package com.example.projectboard.domain.articles.articlehashtags;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.projectboard.domain.articles.QArticle.article;
import static com.example.projectboard.domain.articles.articlehashtags.QArticleHashtag.articleHashtag;
import static com.example.projectboard.domain.hashtags.QHashtag.hashtag;

@RequiredArgsConstructor
public class ArticleHashtagRepositoryImpl implements ArticleHashtagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ArticleHashtag> findByHashtagId(Long hashtagId, Pageable pageable) {
        List<ArticleHashtag> content = queryFactory
                .selectFrom(articleHashtag)
                .leftJoin(articleHashtag.article, article).fetchJoin()
                .leftJoin(articleHashtag.hashtag, hashtag).fetchJoin()
                .where(articleHashtag.hashtag.id.eq(hashtagId))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(getSort(pageable))
                .fetch();

        JPAQuery<ArticleHashtag> countQuery = queryFactory
                .select(articleHashtag)
                .where(articleHashtag.hashtag.id.eq(hashtagId));

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    @Override
    public Page<ArticleHashtag> findByHashtagNameIgnoreCase(String hashtagName, Pageable pageable) {
        List<ArticleHashtag> content = queryFactory
                .selectFrom(articleHashtag)
                .leftJoin(articleHashtag.article, article).fetchJoin()
                .leftJoin(articleHashtag.hashtag, hashtag).fetchJoin()
                .where(articleHashtag.actualHashtagName.containsIgnoreCase(hashtagName))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(getSort(pageable))
                .fetch();

        JPAQuery<ArticleHashtag> countQuery = queryFactory
                .select(articleHashtag)
                .where(articleHashtag.actualHashtagName.equalsIgnoreCase(hashtagName));

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    @Override
    public List<ArticleHashtag> findByHashtagNameIgnoreCase(String hashtagName) {
        return queryFactory
                .selectFrom(articleHashtag)
                .leftJoin(articleHashtag.article, article).fetchJoin()
                .leftJoin(articleHashtag.hashtag, hashtag).fetchJoin()
                .where(articleHashtag.actualHashtagName.containsIgnoreCase(hashtagName))
                .fetch();
    }

    // ?????? ?????? ????????? ?????????
    private OrderSpecifier<?> getSort(Pageable pageable) {
        //??????????????? ????????? Pageable ????????? ???????????? null ??? ??????
        if (!pageable.getSort().isEmpty()) {
            //???????????? ?????? ????????? for ???????????? ?????? ????????????
            for (Sort.Order order : pageable.getSort()) {
                // ??????????????? ????????? DESC or ASC ??? ????????????.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // ??????????????? ????????? ?????? ????????? ????????? ????????? ?????? ???????????? ???????????? ??????.
                switch (order.getProperty()) {
                    case "id":
                        return new OrderSpecifier<>(direction, articleHashtag.id);
                    case "createdAt":
                        return new OrderSpecifier<>(direction, articleHashtag.createdAt);
                    case "article.createdAt":
                        return new OrderSpecifier<>(direction, articleHashtag.article.createdAt);
                    case "article.title":
                        return new OrderSpecifier<>(direction, articleHashtag.article.title);
                }
            }
        }
        return null;
    }
}
