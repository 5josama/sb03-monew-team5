package com.sprint5team.monew.domain.notification.service;

import com.sprint5team.monew.domain.notification.dto.NotificationDto;

import java.util.UUID;

public interface NotificationService {
    NotificationDto notifyArticleForInterest(UUID userId, UUID interestId, String interestName, int articleCount);
    NotificationDto  notifyCommentLiked(UUID userId, UUID commentId, String likerName);
}