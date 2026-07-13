package com.dev.org.model;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.NotificationPriority;
import com.dev.org.domain.NotificationSeverity;
import com.dev.org.domain.NotificationStatus;
import java.time.Instant;

public record UserNotificationQueryDto(
        Long id,
        String title,
        String message,
        String actionUrl,
        String type,
        NotificationPriority priority,
        AudienceType audienceType,
        NotificationSeverity severity,
        NotificationStatus status,
        String[] targets,
        Instant expiresAt,
        Instant createdAt,
        Instant updatedAt,
        Long stateId,
        String userId,
        Instant readAt,
        Instant dismissedAt) {}
