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

@Repository
@RequiredArgsConstructor
@Slf4j
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory query;

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