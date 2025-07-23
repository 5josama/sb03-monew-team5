package com.sprint5team.monew.domain.notification.service;

import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.exception.CommentNotFoundException;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.exception.InterestNotExistsException;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import com.sprint5team.monew.domain.notification.exception.InvalidRequestParameterException;
import com.sprint5team.monew.domain.notification.exception.NotificationNotFoundException;
import com.sprint5team.monew.domain.notification.mapper.NotificationMapper;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
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
        log.info("관심사 기반 알림 생성 요청: userId={}, interestId={}, articleCount={}", userId, interestId, articleCount);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(InterestNotExistsException::new);

        String content = String.format("[%s]와 관련된 기사가 %d건 등록되었습니다.", interestName, articleCount);

        Notification notification = Notification.builder()
                .user(user)
                .interest(interest)
                .content(content)
                .resourceType(ResourceType.INTEREST)
                .confirmed(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("알림 저장 완료: notificationId={}", saved.getId());
        return NotificationDto.from(saved);
    }

    @Override
    public NotificationDto notifyCommentLiked(UUID userId, UUID commentId, String likerName) {
        log.info("댓글 좋아요 알림 생성 요청: userId={}, commentId={}, likerName={}", userId, commentId, likerName);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        String content = String.format("[%s]님이 나의 댓글을 좋아합니다.", likerName);

        Notification notification = Notification.builder()
                .user(user)
                .comment(comment)
                .content(content)
                .resourceType(ResourceType.COMMENT)
                .confirmed(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("알림 저장 완료: notificationId={}", saved.getId());
        return NotificationDto.from(saved);
    }

    @Override
    public CursorPageResponseNotificationDto getAllNotifications(UUID userId, String cursor, Instant after, int limit) {
        log.info("알림 목록 조회 요청: userId={}, cursor={}, after={}, limit={}", userId, cursor, after, limit);

        if (userId == null) {
            log.warn("잘못된 요청: userId가 null");
            throw new InvalidRequestParameterException();
        }

        if (!userRepository.existsById(userId)) {
            log.warn("존재하지 않는 사용자: userId={}", userId);
            throw new UserNotFoundException();
        }

        List<Notification> results = notificationRepository
                .findUnconfirmedNotificationsWithCursorPaging(userId, cursor, after, limit);
        log.debug("조회된 알림 수: {}", results.size());

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
        log.info("알림 목록 조회 완료: userId={}, pageSize={}, hasNext={}, totalElements={}", userId, pageContent.size(), hasNext, totalElements);

        return new CursorPageResponseNotificationDto(
                dtoList,
                nextCursor,
                nextAfter,
                limit,
                totalElements,
                hasNext
        );
    }

    @Override
    public void confirmNotification(UUID notificationId, UUID userId) {
        log.info("단일 알림 확인 요청: userId={}, notificationId={}", userId, notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        notification.confirm();
        log.debug("알림 확인 처리 완료: notificationId={}", notificationId);
    }

    @Override
    public void confirmAllNotifications(UUID userId) {
        log.info("전체 알림 확인 요청: userId={}", userId);

        List<Notification> notifications = notificationRepository.findByUserIdAndConfirmedIsFalse(userId);
        notifications.forEach(Notification::confirm);

        log.info("전체 알림 확인 완료: userId={}, updatedCount={}", userId, notifications.size());
    }

    @Override
    @Transactional
    public void notifyNewArticles() {
        log.info("1시간 이내 등록된 기사와 연관된 관심사를 구독한 유저에게 알림 생성 시작");

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