package com.dev.org.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_notification_states")
public class UserNotificationStateEntity {
    @Id private Long id;

    @Column("notification_id")
    private String notificationId;

    @Column("user_id")
    private String userId;

    @Column("read_at")
    private Instant readAt;

    @Column("dismissed_at")
    private Instant dismissedAt;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
