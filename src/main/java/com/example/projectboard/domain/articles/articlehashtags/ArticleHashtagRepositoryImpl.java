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

    // 정렬 조건 구하는 메서드
    private OrderSpecifier<?> getSort(Pageable pageable) {
        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!pageable.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order order : pageable.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
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
