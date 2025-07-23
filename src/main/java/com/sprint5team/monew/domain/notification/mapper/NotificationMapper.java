package com.sprint5team.monew.domain.notification.mapper;

import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.Notification;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

/**
 * Notification 엔티티를 NotificationDto로 변환하는 MapStruct 매퍼 클래스
 * 기본 매핑 외에도 리소스 타입에 따라 resourceId(Comment 또는 Interest)를 수동으로 설정한다
 */
@Mapper(componentModel = "spring")
public abstract class NotificationMapper {

    /**
     * Notification 엔티티를 NotificationDto로 변환
     *
     * @param notification 알림 엔티티
     * @return 변환된 NotificationDto
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "resourceId", ignore = true)
    public abstract NotificationDto toDto(Notification notification);

    /**
     * resourceType(COMMENT or INTEREST)에 따라 resourceId를 설정
     *
     * @param notification 알림 엔티티
     * @param dto          매핑 대상 DTO 빌더 객체
     */
    @AfterMapping
    protected void setResourceId(Notification notification, @MappingTarget NotificationDto.NotificationDtoBuilder dto) {
        UUID resourceId = switch (notification.getResourceType()) {
            case COMMENT -> notification.getComment() != null ? notification.getComment().getId() : null;
            case INTEREST -> notification.getInterest() != null ? notification.getInterest().getId() : null;
        };
        dto.resourceId(resourceId);
    }
}

