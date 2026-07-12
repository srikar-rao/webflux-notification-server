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
@Table("notifications")
public class NotificationEntity {
    @Id
    private Long id;

    private String title;

    private String message;

    @Column("action_url")
    private String actionUrl;

    private String type;

    private String priority;

    @Column("audience_type")
    private String audienceType;

    private String severity;

    private String status;

    @Column("targets_json")
    private String targetsJson;

    @Column("expires_at")
    private Instant expiresAt;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
