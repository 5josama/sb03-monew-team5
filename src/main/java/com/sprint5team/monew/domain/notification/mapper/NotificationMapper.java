package com.sprint5team.monew.domain.notification.mapper;

import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.Notification;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class NotificationMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "resourceId", ignore = true)
    public abstract NotificationDto toDto(Notification notification);

    @AfterMapping
    protected void setResourceId(Notification notification, @MappingTarget NotificationDto.NotificationDtoBuilder dto) {
        UUID resourceId = switch (notification.getResourceType()) {
            case COMMENT -> notification.getComment() != null ? notification.getComment().getId() : null;
            case INTEREST -> notification.getInterest() != null ? notification.getInterest().getId() : null;
        };
        dto.resourceId(resourceId);
    }
}

