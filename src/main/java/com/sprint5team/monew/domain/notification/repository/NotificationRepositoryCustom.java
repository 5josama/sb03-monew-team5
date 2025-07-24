package com.sprint5team.monew.domain.notification.repository;

import com.sprint5team.monew.domain.notification.entity.Notification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Notification 엔티티에 대한 사용자 정의 쿼리 기능을 정의하는 인터페이스
 */
public interface NotificationRepositoryCustom {

    /**
     * 커서 기반으로 미확인 알림 목록을 조회한다
     * 정렬 기준: createdAt 오름차순
     * 커서와 보조 커서 after를 기준으로 데이터를 페이징한다
     *
     * @param userId 사용자 ID
     * @param cursor 커서 값 (마지막 요소의 createdAt 값 또는 null)
     * @param after  createdAt 기준 보조 커서
     * @param limit  조회할 알림 수
     * @return 조회된 알림 목록
     */
    List<Notification> findUnconfirmedNotificationsWithCursorPaging(UUID userId, String cursor, Instant after, int limit);
}
