package com.sprint5team.monew.domain.notification.repository;

import com.sprint5team.monew.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Notification 엔티티에 대한 데이터 접근을 제공하는 JPA 리포지토리
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID>, NotificationRepositoryCustom {

    /**
     * 특정 사용자의 미확인 알림 개수를 반환
     *
     * @param userId 사용자 ID
     * @return 미확인 알림 개수
     */
    long countByUserIdAndConfirmedIsFalse(UUID userId);

    /**
     * 특정 사용자의 미확인 알림 목록을 조회
     *
     * @param userId 사용자 ID
     * @return 미확인 알림 목록
     */
    List<Notification> findByUserIdAndConfirmedIsFalse(UUID userId);

    /**
     * 확인된 알림 중 7일이 지난 알림을 모두 삭제
     *
     * @param before 삭제 기준 시각
     */
    void deleteByConfirmedIsTrueAndUpdatedAtBefore(Instant before);

    /**
     * 특정 사용자에 대해, 최근 1시간 이내에 동일한 관심사 기반 알림이 존재하는지 확인
     *
     * @param userId     사용자 ID
     * @param interestId 관심사 ID
     * @param createdAt  기준 시각
     * @return 중복 알림 존재 여부
     */
    boolean existsByUserIdAndInterestIdAndCreatedAtAfter(UUID userId, UUID interestId, Instant createdAt);

    /**
     * 특정 사용자의 모든 알림을 삭제
     *
     * @param userId 사용자 ID
     */
    void deleteAllByUserId(UUID userId);
}