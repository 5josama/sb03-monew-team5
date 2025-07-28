package com.sprint5team.monew.domain.notification.service;

import com.sprint5team.monew.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;

import java.time.Instant;
import java.util.UUID;

/**
 * 알림 관련 비즈니스 로직을 정의하는 서비스 인터페이스
 */
public interface NotificationService {

    /**
     * 특정 사용자가 구독한 관심사와 관련된 기사 등록 시 알림을 생성한다
     *
     * @param userId       알림 대상 사용자 ID
     * @param interestId   관련 관심사 ID
     * @param interestName 관심사 이름 (알림 메시지 생성에 사용)
     * @param articleCount 관심사에 해당하는 새 기사 수
     * @return 생성된 알림 DTO
     */
    NotificationDto notifyArticleForInterest(UUID userId, UUID interestId, String interestName, long articleCount);

    /**
     * 작성한 댓글에 좋아요를 받았을 때 알림을 생성한다
     *
     * @param userId     알림 대상 사용자 ID
     * @param commentId  좋아요를 받은 댓글 ID
     * @param likerName  좋아요를 누른 사용자 닉네임
     * @return 생성된 알림 DTO
     */
    NotificationDto  notifyCommentLiked(UUID userId, UUID commentId, String likerName);

    /**
     * 사용자의 미확인 알림 목록을 커서 기반으로 조회한다
     *
     * @param userId 사용자 ID
     * @param cursor 커서 값 (마지막 요소의 createdAt 값 또는 null)
     * @param after  createdAt 기준 보조 커서
     * @param limit  조회할 알림 수
     * @return 커서 기반 알림 목록 응답 DTO
     */
    CursorPageResponseNotificationDto getAllNotifications(UUID userId, String cursor, Instant after, int limit);

    /**
     * 단일 알림 확인 처리
     *
     * @param notificationId 확인할 알림 ID
     * @param userId         사용자 ID
     */
    void confirmNotification(UUID notificationId, UUID userId);

    /**
     * 사용자의 모든 미확인 알림을 일괄 확인 처리한다
     *
     * @param userId 사용자 ID
     */
    void confirmAllNotifications(UUID userId);

    /**
     * 최근 1시간 내에 등록된 기사와 관련된 관심사를 구독 중인 사용자에게 알림을 생성한다
     * 배치 스케줄러에서 사용한다
     */
    void notifyNewArticles();
}