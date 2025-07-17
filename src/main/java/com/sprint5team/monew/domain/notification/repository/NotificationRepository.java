package com.sprint5team.monew.domain.notification.repository;

import com.sprint5team.monew.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID>, NotificationRepositoryCustom {

    long countByUserIdAndConfirmedIsFalse(UUID userId);
    List<Notification> findByUserIdAndConfirmedIsFalse(UUID userId);
}