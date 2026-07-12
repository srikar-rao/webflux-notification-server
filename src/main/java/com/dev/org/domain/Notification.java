package com.dev.org.domain;

import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String id;

    private String title;

    private String message;

    private String actionUrl;

    private String type;

    private NotificationPriority priority;

    private AudienceType audienceType;

    private NotificationSeverity severity;

    private NotificationStatus status;

    private Set<String> targets;

    private Instant expiresAt;

    private Instant createdAt;

    private Instant updatedAt;
}
