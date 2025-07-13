package com.sprint5team.monew.domain.notification.service;

import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;

import java.util.UUID;

public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    @Override
    public NotificationDto notifyArticleForInterest(UUID userId, UUID interestId, String interestName, int articleCount) {
        return null;
    }

    @Override
    public NotificationDto notifyCommentLiked(UUID userId, UUID commentId, String likerName) {
        return null;
    }
}