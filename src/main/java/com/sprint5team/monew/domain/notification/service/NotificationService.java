package com.sprint5team.monew.domain.notification.service;

import com.sprint5team.monew.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;

import java.time.Instant;
import java.util.UUID;

public interface NotificationService {
    NotificationDto notifyArticleForInterest(UUID userId, UUID interestId, String interestName, long articleCount);
    NotificationDto  notifyCommentLiked(UUID userId, UUID commentId, String likerName);
    CursorPageResponseNotificationDto getAllNotifications(UUID userId, String cursor, Instant after, int limit);
    void confirmNotification(UUID notificationId, UUID userId);
    void confirmAllNotifications(UUID userId);
    void notifyNewArticles();
}