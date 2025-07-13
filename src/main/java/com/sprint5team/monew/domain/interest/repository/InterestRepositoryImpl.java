package com.sprint5team.monew.domain.interest.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.entity.QInterest;
import com.sprint5team.monew.domain.keyword.entity.QKeyword;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.repository
 * FileName     : InterestRepositoryImpl
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
@RequiredArgsConstructor
public class InterestRepositoryImpl implements InterestRepositoryCustom{

    private static final String NAME = "name";
    private static final String SUBSCRIBER_COUNT = "subscriberCount";

    private final JPAQueryFactory queryFactory;


    @Override
    public long countTotalElements(CursorPageRequest request) {
        
        return 0;
    }

    @Override
    public List<Interest> findAllInterestByRequest(CursorPageRequest request) {

        QInterest interest = QInterest.interest;
        QKeyword keyword = QKeyword.keyword;
        BooleanBuilder where = new BooleanBuilder();

        Order direction = request.getDirectionAsOrder();
        


        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            where.and(interest.name.containsIgnoreCase(request.getKeyword())
                .or(JPAExpressions.selectOne()
                    .from(keyword)
                    .where(keyword.interest.eq(interest)
                        .and(keyword.name.containsIgnoreCase(request.getKeyword())))
                    .exists()));
        }

        if(request.getCursor()!= null && request.getAfter() != null) {
            BooleanBuilder cursorCondition = new BooleanBuilder();
            if (request.getOrderBy().equals(NAME)) {
                if (direction == Order.ASC) {
                    cursorCondition.or(
                            interest.name.gt(request.getCursor()))
                        .or(interest.name.eq(request.getCursor()).and(interest.createdAt.gt(request.getAfter())));

                } else if (direction == Order.DESC) {
                    cursorCondition.or(
                            interest.name.lt(request.getCursor()))
                        .or(interest.name.eq(request.getCursor()).and(interest.createdAt.lt(request.getAfter())));
                }
            }
            else if (request.getOrderBy().equals(SUBSCRIBER_COUNT)) {
                if (direction == Order.ASC) {
                    cursorCondition.or(
                            interest.subscriberCount.gt(Long.valueOf(request.getCursor())))
                        .or(interest.subscriberCount.eq(Long.valueOf(request.getCursor())).and(interest.createdAt.gt(request.getAfter())));
                }
                 else if (direction == Order.DESC) {
                    cursorCondition.or(
                            interest.subscriberCount.lt(Long.valueOf(request.getCursor())))
                        .or(interest.subscriberCount.eq(Long.valueOf(request.getCursor())).and(interest.createdAt.lt(request.getAfter())));
                }
            }
            where.and(cursorCondition);
        }

        OrderSpecifier<?> primaryOrder = getOrderSpecifier(request.getOrderBy(), direction, interest);
        OrderSpecifier<?> secondaryOrder = direction == Order.ASC ? interest.createdAt.asc() : interest.createdAt.desc();

        List<Interest> interests = queryFactory
            .selectFrom(interest)
            .where(where)
            .orderBy(primaryOrder, secondaryOrder)
            .limit(request.getLimit() + 1)
            .fetch();


        return interests;
    }

    private OrderSpecifier<?> getOrderSpecifier(String orderBy, Order direction, QInterest interest) {
        return switch (orderBy) {
            case NAME -> new OrderSpecifier<>(direction, interest.name);
            default -> new OrderSpecifier<>(direction, interest.subscriberCount);
        };
    }
}
