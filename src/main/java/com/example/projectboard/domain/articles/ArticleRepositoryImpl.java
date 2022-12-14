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
                        containsCreatedBy(condition.getCreatedBy()),
                        eqCreatedAt(condition.getCreatedAt())
                )
                .orderBy(article.createdAt.desc())
                .fetch();
    }

    @Override
    public Page<Article> findAll(ArticleSearchCondition condition, Pageable pageable) {

        var content
                = queryFactory.selectFrom(article)
                .where(containsTitle(condition.getTitle()),
                        containsCreatedBy(condition.getCreatedBy()),
                        eqCreatedAt(condition.getCreatedAt())
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(pageable))
                .fetch();

        var countQuery
                = queryFactory.selectFrom(article)
                .where(containsTitle(condition.getTitle()),
                        containsCreatedBy(condition.getCreatedBy()),
                        eqCreatedAt(condition.getCreatedAt())
                );

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    @Override
    public Page<Article> findAllByUserId(Long userId, ArticleSearchCondition condition, Pageable pageable) {
        var content
                = queryFactory.selectFrom(article)
                .where(containsTitle(condition.getTitle()),
                        eqCreatedAt(condition.getCreatedAt()),
                        eqUserId(userId)
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(pageable))
                .fetch();

        var countQuery
                = queryFactory.selectFrom(article)
                .where(containsTitle(condition.getTitle()),
                        eqCreatedAt(condition.getCreatedAt()),
                        eqUserId(userId));

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    // ???????????? BooleanExpression ?????????
    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? article.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression containsTitle(String title) {
        return StringUtils.hasText(title) ? article.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression eqUserId(Long userId) {
        return Objects.nonNull(userId) ? article.userId.eq(userId) : null;
    }

    private BooleanExpression eqCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ? Objects.requireNonNull(goeCreatedAt(createdAt)).and(ltCreatedAt(createdAt)) : null;
    }

    private BooleanExpression goeCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ? article.createdAt.goe(createdAt) : null;
    }

    private BooleanExpression ltCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ? article.createdAt.lt(createdAt.plusDays(1)) : null;
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
                        return new OrderSpecifier<>(direction, article.id);
                    case "title":
                        return new OrderSpecifier<>(direction, article.title);
                    case "viewCount":
                        return new OrderSpecifier<>(direction, article.viewCount);
                    case "createdBy":
                        return new OrderSpecifier<>(direction, article.createdBy);
                    case "createdAt":
                        return new OrderSpecifier<>(direction, article.createdAt);
                    case "userId":
                        return new OrderSpecifier<>(direction, article.userId);
                }
            }
        }
        return null;
    }

}
