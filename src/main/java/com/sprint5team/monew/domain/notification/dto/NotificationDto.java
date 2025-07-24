package com.sprint5team.monew.domain.notification.dto;

import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * @param id            알림 ID (UUID)
 * @param createdAt     생성된 날짜/시간
 * @param updatedAt     확인한 날짜/시간 (또는 마지막 수정일)
 * @param confirmed     알림 확인 여부 (true: 확인함, false: 미확인)
 * @param userId        알림 대상 사용자 ID
 * @param content       알림 내용
 * @param resourceType  관련된 리소스 유형 ("interest" 또는 "comment")
 * @param resourceId    관련된 리소스의 ID (관심사 또는 댓글 ID)
 */
@Builder
public record NotificationDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        boolean confirmed,
        UUID userId,
        String content,
        ResourceType resourceType,
        UUID resourceId
) {
    /**
     * Notification 엔티티 객체를 DTO로 변환
     * resourceType 값에 따라  comment 또는 interest 중
     * 하나의 ID를 resourceId 필드에 설정
     * @param notification 변환할 Notification 엔티티
     * @return NotificationDto로 변환된 결과
     */
    public static NotificationDto from(Notification notification) {
        UUID resourceId = switch (notification.getResourceType()) {
            case COMMENT -> notification.getComment() != null ? notification.getComment().getId() : null;
            case INTEREST -> notification.getInterest() != null ? notification.getInterest().getId() : null;
        };

        return NotificationDto.builder()
                .id(notification.getId())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .confirmed(notification.isConfirmed())
                .userId(notification.getUser().getId())
                .content(notification.getContent())
                .resourceType(notification.getResourceType())
                .resourceId(resourceId)
                .build();
    }
}