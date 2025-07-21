package com.sprint5team.monew.domain.notification.service;

import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import com.sprint5team.monew.domain.notification.mapper.NotificationMapper;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final CommentRepository commentRepository;
    private final NotificationMapper notificationMapper;
    private final ArticleRepository articleRepository;
    private final UserInterestRepository userInterestRepository;

    @Override
    public NotificationDto notifyArticleForInterest(UUID userId, UUID interestId, String interestName, long articleCount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new IllegalArgumentException("관심사를 찾을 수 없습니다."));

        String content = String.format("[%s]와 관련된 기사가 %d건 등록되었습니다.", interestName, articleCount);

        Notification notification = Notification.builder()
                .user(user)
                .interest(interest)
                .content(content)
                .resourceType(ResourceType.INTEREST)
                .confirmed(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        return NotificationDto.from(saved);
    }

    @Override
    public NotificationDto notifyCommentLiked(UUID userId, UUID commentId, String likerName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        String content = likerName + "님이 내 댓글을 좋아했습니다.";

        Notification notification = Notification.builder()
                .user(user)
                .comment(comment)
                .content(content)
                .resourceType(ResourceType.COMMENT)
                .confirmed(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        return NotificationDto.from(saved);
    }

    @Override
    public CursorPageResponseNotificationDto getAllNotifications(UUID userId, String cursor, Instant after, int limit) {
        List<Notification> results = notificationRepository
                .findUnconfirmedNotificationsWithCursorPaging(userId, cursor, after, limit);

        boolean hasNext = results.size() > limit;

        List<Notification> pageContent = hasNext ? results.subList(0, limit) : results;

        String nextCursor = null;
        Instant nextAfter = null;
        if (hasNext && !pageContent.isEmpty()) {
            Notification last = pageContent.get(pageContent.size() - 1);
            nextAfter = last.getCreatedAt();
            nextCursor = last.getCreatedAt().toString();
        }

        List<NotificationDto> dtoList = pageContent.stream()
                .map(notificationMapper::toDto)
                .toList();

        long totalElements = notificationRepository.countByUserIdAndConfirmedIsFalse(userId);

        return new CursorPageResponseNotificationDto(
                dtoList,
                nextCursor,
                nextAfter,
                dtoList.size(),
                totalElements,
                hasNext
        );
    }

    @Override
    public void confirmNotification(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("알림을 찾을 수 없습니다."));

        notification.confirm();
    }

    @Override
    public void confirmAllNotifications(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndConfirmedIsFalse(userId);
        notifications.forEach(Notification::confirm);
    }

    @Override
    @Transactional
    public void notifyNewArticles() {
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        List<Interest> interests = interestRepository.findAll();

        for (Interest interest : interests) {
            long count = articleRepository.countRecentArticlesByInterestId(interest.getId(), oneHourAgo);
            if (count == 0) continue;

            List<User> users = userInterestRepository.findUsersByInterestId(interest.getId());
            if (users.isEmpty()) continue;

            for (User user : users) {
                boolean alreadyNotified = notificationRepository.existsByUserIdAndInterestIdAndCreatedAtAfter(
                        user.getId(), interest.getId(), oneHourAgo
                );
                if (alreadyNotified) continue;

                notifyArticleForInterest(
                        user.getId(), interest.getId(), interest.getName(), count
                );
            }
        }

        log.info("알림 생성 완료");
    }

}