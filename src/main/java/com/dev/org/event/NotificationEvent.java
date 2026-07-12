package com.dev.org.event;

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
public class NotificationEvent {
    private String id;
    private String title;
    private String message;
    private String actionUrl;
    private String type;
    private String priority;
    private String audienceType;
    private String severity;
    private String status;
    private Set<String> targets;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;
}
