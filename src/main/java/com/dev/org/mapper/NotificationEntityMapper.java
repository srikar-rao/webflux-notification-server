package com.dev.org.mapper;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationPriority;
import com.dev.org.domain.NotificationSeverity;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.entity.NotificationEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class NotificationEntityMapper {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public NotificationEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
                .targetsJson(serializeTargets(notification.getTargets()))
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
                .targets(deserializeTargets(entity.getTargetsJson()))
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private String serializeTargets(Set<String> targets) {
        if (targets == null || targets.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(targets);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Unable to serialize notification targets", ex);
        }
    }

    private Set<String> deserializeTargets(String targetsJson) {
        if (targetsJson == null || targetsJson.isBlank()) {
            return null;
        }
        try {
            return new LinkedHashSet<>(objectMapper.readValue(targetsJson, STRING_LIST_TYPE));
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Unable to deserialize notification targets", ex);
        }
    }
}
