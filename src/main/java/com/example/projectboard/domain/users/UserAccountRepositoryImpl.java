package com.example.projectboard.domain.users;

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

import static com.example.projectboard.domain.users.QUserAccount.userAccount;

@RequiredArgsConstructor
public class UserAccountRepositoryImpl implements UserAccountRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자 리스트 페이징 조회 by 검색 조건
     * @param condition : UserAccount 검색 조건 (UserAccountSearchCondition)
     * @param pageable : 페이징 정보 (Pageable)
     * @return Page<UserAccount> : 검색 조건에 따른 UserAccount 페이징 결과 반환.
     */
    @Override
    public Page<UserAccount> findAllBySearchCondition(UserAccountSearchCondition condition, Pageable pageable) {

        var content = queryFactory.selectFrom(userAccount)
                .where(containsUsername(condition.getUsername()),
                        containsEmail(condition.getEmail()),
                        eqPhoneNumber(condition.getPhoneNumber()),
                        eqCreatedAt(condition.getCreatedAt())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(pageable))
                .fetch();

        var countQuery = queryFactory.selectFrom(userAccount)
                .where(containsUsername(condition.getUsername()),
                        containsEmail(condition.getEmail()),
                        eqPhoneNumber(condition.getPhoneNumber()));

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    /**
     * 사용자 리스트 조회 by 검색조건 (조회 결과: username 사전순 정렬)
     * @param condition : UserAccount 검색 조건 (UserAccountSearchCondition)
     * @return List<UserAccount> : 검색 결과에 따른 모든 UserAccount 리스트 반환 (username 사전순 정렬)
     */
    @Override
    public List<UserAccount> findAllBySearchCondition(UserAccountSearchCondition condition) {
        return queryFactory.selectFrom(userAccount)
                .where(containsUsername(condition.getUsername()),
                        containsEmail(condition.getEmail()),
                        eqPhoneNumber(condition.getPhoneNumber()),
                        eqCreatedAt(condition.getCreatedAt())
                )
                .orderBy(userAccount.username.asc())
                .fetch();
    }

    private BooleanExpression containsUsername(String username) {
        return (StringUtils.hasText(username)) ? userAccount.username.containsIgnoreCase(username) : null;
    }

    private BooleanExpression containsEmail(String email) {
        return (StringUtils.hasText(email)) ? userAccount.email.containsIgnoreCase(email) : null;
    }

    private BooleanExpression eqPhoneNumber(String phoneNumber) {
        return (StringUtils.hasText(phoneNumber)) ? userAccount.phoneNumber.eq(phoneNumber) : null;
    }

    private BooleanExpression eqCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ? Objects.requireNonNull(goeCreatedAt(createdAt)).and(ltCreatedAt(createdAt)) : null;
    }

    private BooleanExpression goeCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ? userAccount.createdAt.goe(createdAt) : null;
    }

    private BooleanExpression ltCreatedAt(LocalDateTime createdAt) {
        return Objects.nonNull(createdAt) ? userAccount.createdAt.lt(createdAt.plusDays(1)) : null;
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
                        return new OrderSpecifier<>(direction, userAccount.id);
                    case "username":
                        return new OrderSpecifier<>(direction, userAccount.username);
                    case "email":
                        return new OrderSpecifier<>(direction, userAccount.email);
                    case "phoneNumber":
                        return new OrderSpecifier<>(direction, userAccount.phoneNumber);
                    case "createdAt":
                        return new OrderSpecifier<>(direction, userAccount.createdAt);
                }
            }
        }
        return null;
    }
}
