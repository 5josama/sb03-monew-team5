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

/**
 * NotificationService 구현체로, 알림 생성, 조회, 확인 등의 비즈니스 로직을 처리한다
 */
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

    /**
     * 특정 사용자가 구독한 관심사와 관련된 기사 등록 시 알림을 생성한다
     *
     * @param userId       알림 대상 사용자 ID
     * @param interestId   관심사 ID
     * @param interestName 관심사 이름
     * @param articleCount 관련된 기사 수
     * @return 생성된 알림 DTO
     * @throws UserNotFoundException      사용자가 존재하지 않는 경우 발생
     * @throws InterestNotExistsException 관심사가 존재하지 않는 경우 발생
     */
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

    /**
     * 특정 댓글에 대한 좋아요가 발생했을 때, 해당 댓글 작성자에게 알림을 생성한다
     *
     * @param userId     알림 대상 사용자 ID
     * @param commentId  좋아요가 눌린 댓글 ID
     * @param likerName  좋아요를 누른 사용자 이름
     * @return 생성된 알림 정보
     * @throws UserNotFoundException    사용자가 존재하지 않는 경우 발생
     * @throws CommentNotFoundException 댓글이 존재하지 않는 경우 발생
     */
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

    /**
     * 사용자의 미확인 알림 목록을 커서 기반 페이지네이션으로 조회한다
     *
     * @param userId 사용자 ID
     * @param cursor 커서 값 (마지막 요소의 createdAt 값 또는 null)
     * @param after  createdAt 기준 보조 커서
     * @param limit  조회할 알림 수
     * @return 커서 기반 페이징 응답 객체
     * @throws InvalidRequestParameterException userId가 null인 경우 발생
     * @throws UserNotFoundException            사용자가 존재하지 않는 경우 발생
     */
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

    /**
     * 특정 알림을 확인 상태(true)로 변경한다
     *
     * @param notificationId 알림 ID
     * @param userId         사용자 ID
     * @throws NotificationNotFoundException 알림이 존재하지 않는 경우 발생
     */
    @Override
    public void confirmNotification(UUID notificationId, UUID userId) {
        log.info("단일 알림 확인 요청: userId={}, notificationId={}", userId, notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        notification.confirm();
        log.debug("알림 확인 처리 완료: notificationId={}", notificationId);
    }

    /**
     * 해당 사용자의 모든 미확인 알림을 일괄 확인 처리한다
     *
     * @param userId 사용자 ID
     */
    @Override
    public void confirmAllNotifications(UUID userId) {
        log.info("전체 알림 확인 요청: userId={}", userId);

        List<Notification> notifications = notificationRepository.findByUserIdAndConfirmedIsFalse(userId);
        notifications.forEach(Notification::confirm);

        log.info("전체 알림 확인 완료: userId={}, updatedCount={}", userId, notifications.size());
    }

    /**
     * 1시간 이내 등록된 기사 중, 관심사와 관련된 기사 수가 있는 경우
     * 해당 관심사를 구독 중인 사용자에게 중복 알림 없이 알림을 생성한다
     */
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