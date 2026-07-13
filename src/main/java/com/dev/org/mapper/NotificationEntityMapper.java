package com.dev.org.mapper;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationPriority;
import com.dev.org.domain.NotificationSeverity;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationEntityMapper {

    public NotificationEntity toEntity(Notification notification) {
        return NotificationEntity.builder()
                .id(notification.getId() != null ? Long.valueOf(notification.getId()) : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .actionUrl(notification.getActionUrl())
                .type(notification.getType())
                .priority(notification.getPriority().name())
                .audienceType(notification.getAudienceType().name())
                .severity(notification.getSeverity().name())
                .status(notification.getStatus().name())
                .targets(notification.getTargets())
                .expiresAt(notification.getExpiresAt())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }

    public Notification toDomain(NotificationEntity entity) {
        return Notification.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .title(entity.getTitle())
                .message(entity.getMessage())
                .actionUrl(entity.getActionUrl())
                .type(entity.getType())
                .priority(NotificationPriority.valueOf(entity.getPriority()))
                .audienceType(AudienceType.valueOf(entity.getAudienceType()))
                .severity(NotificationSeverity.valueOf(entity.getSeverity()))
                .status(NotificationStatus.valueOf(entity.getStatus()))
                .targets(entity.getTargets())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
