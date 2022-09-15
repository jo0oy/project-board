package com.example.projectboard.domain.articlecomments;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
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
import java.util.Optional;

import static com.example.projectboard.domain.articlecomments.QArticleComment.articleComment;

@RequiredArgsConstructor
public class ArticleCommentRepositoryImpl implements ArticleCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ArticleCommentInfo.MainInfo> findWithArticleId(Long id) {
        return Optional.ofNullable(
                queryFactory.select(
                        Projections.fields(ArticleCommentInfo.MainInfo.class,
                                articleComment.id.as("commentId"),
                                articleComment.article.id.as("articleId"),
                                articleComment.content,
                                articleComment.createdBy,
                                articleComment.createdAt))
                        .from(articleComment)
                        .where(articleComment.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public Page<ArticleComment> findByArticleId(Long articleId, Pageable pageable) {
        var content =
                queryFactory.selectFrom(articleComment)
                .where(articleComment.article.id.eq(articleId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(pageable))
                .fetch();

        var countQuery
                = queryFactory.selectFrom(articleComment)
                .where(articleComment.article.id.eq(articleId));

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    @Override
    public List<ArticleCommentInfo.MainInfo> findAllWithArticleId() {
        return queryFactory.select(
                Projections.fields(ArticleCommentInfo.MainInfo.class,
                articleComment.id.as("commentId"),
                articleComment.article.id.as("articleId"),
                articleComment.content,
                articleComment.createdBy,
                articleComment.createdAt)
                ).from(articleComment)
                .orderBy(articleComment.id.desc())
                .fetch();
    }

    @Override
    public Page<ArticleCommentInfo.MainInfo> findAllWithArticleId(Pageable pageable) {

        var content
                = queryFactory.select(
                Projections.fields(ArticleCommentInfo.MainInfo.class,
                        articleComment.id.as("commentId"),
                        articleComment.article.id.as("articleId"),
                        articleComment.content,
                        articleComment.createdBy,
                        articleComment.createdAt)
                ).from(articleComment)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(pageable))
                .fetch();

        var countQuery
                = queryFactory.selectFrom(articleComment);

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    @Override
    public List<ArticleCommentInfo.MainInfo> findAllWithArticleId(ArticleCommentSearchCondition condition) {
        return queryFactory.select(
                        Projections.fields(ArticleCommentInfo.MainInfo.class,
                                articleComment.id.as("commentId"),
                                articleComment.article.id.as("articleId"),
                                articleComment.content,
                                articleComment.createdBy,
                                articleComment.createdAt)
                )
                .from(articleComment)
                .where(containsCreatedBy(condition.getCreatedBy()),
                        eqCreatedAt(condition.getCreatedAt())
                )
                .orderBy(articleComment.id.desc())
                .fetch();
    }

    @Override
    public Page<ArticleCommentInfo.MainInfo> findAllWithArticleId(ArticleCommentSearchCondition condition, Pageable pageable) {
        var content
                = queryFactory.select(
                        Projections.fields(ArticleCommentInfo.MainInfo.class,
                                articleComment.id.as("commentId"),
                                articleComment.article.id.as("articleId"),
                                articleComment.content,
                                articleComment.createdBy,
                                articleComment.createdAt)
                )
                .from(articleComment)
                .where(containsCreatedBy(condition.getCreatedBy()),
                        eqCreatedAt(condition.getCreatedAt())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(pageable))
                .fetch();

        var countQuery
                = queryFactory.selectFrom(articleComment)
                .where(containsCreatedBy(condition.getCreatedBy()),
                        eqCreatedAt(condition.getCreatedAt()));

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? articleComment.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression eqCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ?
                articleComment.createdAt.goe(createdAt)
                        .and(articleComment.createdAt.lt(createdAt.plusDays(1))) : null;
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
                        return new OrderSpecifier<>(direction, articleComment.id);
                    case "createdBy":
                        return new OrderSpecifier<>(direction, articleComment.createdBy);
                }
            }
        }
        return null;
    }
}
