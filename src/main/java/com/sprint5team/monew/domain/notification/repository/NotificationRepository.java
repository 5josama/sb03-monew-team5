package com.sprint5team.monew.domain.notification.repository;

import com.sprint5team.monew.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID>, NotificationRepositoryCustom {

    long countByUserIdAndConfirmedIsFalse(UUID userId);
    List<Notification> findByUserIdAndConfirmedIsFalse(UUID userId);
    void deleteByConfirmedIsTrueAndCreatedAtBefore(Instant before);
    boolean existsByUserIdAndInterestIdAndCreatedAtAfter(UUID userId, UUID interestId, Instant createdAt);
    // 사용자 물리삭제 시 사용
    void deleteAllByUserId(UUID userId);
}