package com.sprint5team.monew.domain.notification.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.entity.QNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * NotificationRepositoryCustom의 구현체로,
 * QueryDSL을 이용해 알림 데이터를 커서 기반으로 페이징 조회한다
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory query;

    /**
     * 커서 기반으로 미확인 알림 목록을 조회
     * createdAt 기준 오름차순 정렬
     * after 파라미터가 주어지면 해당 시간 이후 알림만 조회
     * 최대 limit + 1개를 조회하여 다음 페이지 여부 판단
     *
     * @param userId 사용자 ID
     * @param cursor 커서 값 (현재 사용하지 않지만, ID 기반 커서 추가 가능성 고려)
     * @param after  createdAt 기준 보조 커서
     * @param limit  페이지 크기
     * @return 커서 기반 미확인 알림 목록
     */
    @Override
    public List<Notification> findUnconfirmedNotificationsWithCursorPaging(UUID userId, String cursor, Instant after, int limit) {
        QNotification notification = QNotification.notification;

        BooleanBuilder builder = new BooleanBuilder()
                .and(notification.user.id.eq(userId))
                .and(notification.confirmed.isFalse());

        if (after != null) {
            builder.and(notification.createdAt.gt(after));
        }

        log.debug("알림 커서 기반 페이징 쿼리 실행: userId={}, cursor={}, after={}, limit={}", userId, cursor, after, limit);

        return query.selectFrom(notification)
                .where(builder)
                .orderBy(notification.createdAt.asc())
                .limit(limit + 1)
                .fetch();
    }
}