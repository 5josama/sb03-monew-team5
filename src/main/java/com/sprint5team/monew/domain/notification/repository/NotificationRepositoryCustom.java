package com.sprint5team.monew.domain.notification.repository;

import com.sprint5team.monew.domain.notification.entity.Notification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationRepositoryCustom {
    List<Notification> findUnconfirmedNotificationsWithCursorPaging(UUID userId, String cursor, Instant after, int limit);
}
