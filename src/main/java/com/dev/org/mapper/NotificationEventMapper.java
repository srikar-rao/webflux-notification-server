package com.dev.org.mapper;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationPriority;
import com.dev.org.domain.NotificationSeverity;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.event.NotificationEvent;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventMapper {

    public NotificationEvent toEvent(Notification notification) {
        return NotificationEvent.builder()
                .id(notification.getId())
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

    public Notification toDomain(NotificationEvent notificationEvent) {
        return Notification.builder()
                .id(notificationEvent.getId())
                .title(notificationEvent.getTitle())
                .message(notificationEvent.getMessage())
                .actionUrl(notificationEvent.getActionUrl())
                .type(notificationEvent.getType())
                .priority(NotificationPriority.valueOf(notificationEvent.getPriority()))
                .audienceType(AudienceType.valueOf(notificationEvent.getAudienceType()))
                .severity(NotificationSeverity.valueOf(notificationEvent.getSeverity()))
                .status(NotificationStatus.valueOf(notificationEvent.getStatus()))
                .targets(notificationEvent.getTargets())
                .expiresAt(notificationEvent.getExpiresAt())
                .createdAt(notificationEvent.getCreatedAt())
                .updatedAt(notificationEvent.getUpdatedAt())
                .build();
    }
}
