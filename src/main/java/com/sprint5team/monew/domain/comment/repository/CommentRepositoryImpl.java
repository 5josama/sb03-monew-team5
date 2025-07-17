package com.sprint5team.monew.domain.comment.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.entity.QComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QComment comment = QComment.comment;

    @Override
    public long countTotalElements(UUID articleId) {
        return Optional.ofNullable(
                queryFactory
                        .select(comment.count())
                        .from(comment)
                        .where(comment.article.id.eq(articleId),                // articleId가 일치하는 comment의 갯수 찾기
                                comment.isDeleted.eq(false))              // 논리삭제된 댓글은 제외
                        .fetchOne()
        ).orElse(0L);
    }



    @Override
    public List<Comment> findCommentsWithCursor(UUID articleId, String cursor, Instant after, Pageable pageable) {

        //정렬 순서 찾기
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        String property = sortOrder.getProperty();
        Order order = sortOrder.isAscending() ? Order.ASC : Order.DESC;

        return queryFactory
                .selectFrom(comment)                                            // 조건에 만족하는 comment의 내용 모두 검색
                .where(
                        comment.article.id.eq(articleId),                       // articleId가 같은경우 이면서
                        buildCursorCondition(cursor, after, property, order),   // buildCursorCondition을 만족하는경우,
                        comment.isDeleted.eq(false)                       // 댓글이 논리삭제된 경우를 제외,
                )
                .orderBy(getOrderSpecifier(property, order))                    // 해당 순서로 정렬
                .limit(pageable.getPageSize())                                  // 해당 갯수 불러오기
                .fetch();                                                       // 검색 실행
    }


    /**
     * 커서의 property를 기준으로 검색범위를 지정하는 메서드
     * @param cursor 검색하길 원하는 커서
     * @param after  보조 검색자(createdAt)
     * @param property  정렬할 필드 (createdAt, likeCount)
     * @param order 정렬 순서
     * @return  검색 범위 조건
     */
    private BooleanExpression buildCursorCondition(String cursor, Instant after, String property, Order order){
        if(cursor == null && after == null){
            return null;
        }

        switch (property){
            case "createdAt":
                if(after == null) return null;

                if(order.equals(Order.ASC)){
                    return comment.createdAt.gt(after);
                } else{
                    return comment.createdAt.lt(after);
                }


            case "likeCount":
                if (cursor == null) return null;

                if(order.equals(Order.ASC)){
                    return comment.likeCount.gt(Long.valueOf(cursor));
                } else{
                    return comment.likeCount.lt(Long.valueOf(cursor));
                }

            default:
                if(order.equals(Order.ASC)){
                    return comment.createdAt.gt(after);
                } else{
                    return comment.createdAt.lt(after);
                }
        }
    }

    /**
     * 정렬 조건 생성 메서드
     *
     * @param property 정렬하길 원하는 필드
     * @param order    정렬방법
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> getOrderSpecifier(String property, Order order) {
        switch (property) {
            case "createdAt":
                return new OrderSpecifier<>(order, comment.createdAt);

            case "likeCount":
                return new OrderSpecifier<>(order, comment.likeCount);

            default:
                return new OrderSpecifier<>(Order.DESC, comment.createdAt);
        }
    }
}
