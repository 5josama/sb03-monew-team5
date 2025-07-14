package com.sprint5team.monew.domain.notification.repository;

import com.sprint5team.monew.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByUserIdAndConfirmedIsFalseWithCursorPaging(UUID userId, String cursor, Instant after, int limit);
}