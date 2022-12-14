package com.example.projectboard.domain.likes;

import com.example.projectboard.domain.users.QUserAccount;
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
import java.util.Objects;

import static com.example.projectboard.domain.articles.QArticle.article;
import static com.example.projectboard.domain.likes.QLike.like;
import static com.example.projectboard.domain.users.QUserAccount.*;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Like> articleLikesByUsername(String username, ArticleLikeSearchCondition condition, Pageable pageable) {
        var content =  queryFactory.selectFrom(like)
                .join(like.article, article).fetchJoin()
                .join(like.userAccount, userAccount)
                .where(eqUsername(username),
                        containsCreatedBy(condition.getCreatedBy()),
                        containsTitle(condition.getTitle()),
                        eqCreatedAt(condition.getCreatedAt()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(pageable))
                .fetch();

        var countQuery = queryFactory.selectFrom(like)
                .join(like.article, article).fetchJoin()
                .where(eqUsername(username),
                        containsCreatedBy(condition.getCreatedBy()),
                        containsTitle(condition.getTitle()),
                        eqCreatedAt(condition.getCreatedAt()));

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    private BooleanExpression eqUsername(String username) {
        return StringUtils.hasText(username) ? like.userAccount.username.eq(username) : null;
    }

    private BooleanExpression containsTitle(String title) {
        return StringUtils.hasText(title) ? like.article.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? like.article.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression eqCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ?
                Objects.requireNonNull(goeCreatedAt(createdAt)).and(ltCreatedAt(createdAt)) : null;
    }

    private BooleanExpression goeCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ? like.article.createdAt.goe(createdAt) : null;
    }

    private BooleanExpression ltCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ? like.article.createdAt.lt(createdAt.plusDays(1)) : null;
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
                        return new OrderSpecifier<>(direction, like.id);
                    case "createdAt":
                        return new OrderSpecifier<>(direction, like.createdAt);
                    case "article.id":
                        return new OrderSpecifier<>(direction, like.article.id);
                    case "title":
                        return new OrderSpecifier<>(direction, like.article.title);
                    case "createdBy":
                        return new OrderSpecifier<>(direction, like.article.createdBy);
                    case "article.createdAt":
                        return new OrderSpecifier<>(direction, like.article.createdAt);
                    case "userId":
                        return new OrderSpecifier<>(direction, like.article.userId);
                }
            }
        }
        return null;
    }
}
