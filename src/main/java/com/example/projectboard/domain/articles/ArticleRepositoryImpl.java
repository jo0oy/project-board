package com.example.projectboard.domain.articles;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.example.projectboard.domain.articles.QArticle.article;

@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Article> findAll(ArticleSearchCondition condition) {
        return queryFactory.selectFrom(article)
                .where(containsTitle(condition.getTitle()),
                        containsHashtag(condition.getHashtag()),
                        containsCreatedBy(condition.getCreatedBy()),
                        goeCreatedAt(condition.getCreatedAt()),
                        ltCreatedAt(condition.getCreatedAt())
                )
                .orderBy(article.id.desc())
                .fetch();
    }

    @Override
    public Page<Article> findAll(ArticleSearchCondition condition, Pageable pageable) {

        var content
                = queryFactory.selectFrom(article)
                .where(containsTitle(condition.getTitle()),
                        containsHashtag(condition.getHashtag()),
                        containsCreatedBy(condition.getCreatedBy()),
                        goeCreatedAt(condition.getCreatedAt()),
                        ltCreatedAt(condition.getCreatedAt())
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(pageable))
                .fetch();

        var countQuery
                = queryFactory.selectFrom(article)
                .where(containsTitle(condition.getTitle()),
                        containsHashtag(condition.getHashtag()),
                        containsCreatedBy(condition.getCreatedBy()),
                        goeCreatedAt(condition.getCreatedAt()),
                        ltCreatedAt(condition.getCreatedAt())
                );

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    // 검색조건 BooleanExpression 메서드
    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? article.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression containsTitle(String title) {
        return StringUtils.hasText(title) ? article.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression containsHashtag(String hashtag) {
        return StringUtils.hasText(hashtag) ? article.hashtag.containsIgnoreCase(hashtag) : null;
    }

    private BooleanExpression goeCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ? article.createdAt.goe(createdAt) : null;
    }

    private BooleanExpression ltCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ? article.createdAt.lt(createdAt.plusDays(1)) : null;
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
                        return new OrderSpecifier<>(direction, article.id);
                    case "title":
                        return new OrderSpecifier<>(direction, article.title);
                    case "createdBy":
                        return new OrderSpecifier<>(direction, article.createdBy);
                }
            }
        }
        return null;
    }

}
