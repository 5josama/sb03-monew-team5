package com.sprint5team.monew.domain.notification.repository;

import com.sprint5team.monew.domain.notification.entity.Notification;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository {
    Notification save(Notification notification);
}