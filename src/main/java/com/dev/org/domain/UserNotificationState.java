package com.dev.org.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationState {
    private String id;

    private String notificationId;

    private String userId;

    private Instant readAt;

    private Instant dismissedAt;

    private Instant createdAt;

    private Instant updatedAt;
}
