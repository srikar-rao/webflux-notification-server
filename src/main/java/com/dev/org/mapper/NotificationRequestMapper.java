package com.dev.org.mapper;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationPriority;
import com.dev.org.domain.NotificationSeverity;
import com.dev.org.model.CreateNotificationRequest;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class NotificationRequestMapper {

    public Notification toDomain(CreateNotificationRequest request) {
        Set<String> targets =
                request.getTargets() == null ? null : new LinkedHashSet<>(request.getTargets());

        return Notification.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .actionUrl(request.getActionUrl())
                .type(request.getType())
                .priority(NotificationPriority.valueOf(request.getPriority().getValue()))
                .audienceType(AudienceType.valueOf(request.getAudienceType().getValue()))
                .severity(NotificationSeverity.valueOf(request.getSeverity().getValue()))
                .targets(targets)
                .expiresAt(request.getExpiresAt() != null ? request.getExpiresAt().toInstant() : null)
                .build();
    }
}
