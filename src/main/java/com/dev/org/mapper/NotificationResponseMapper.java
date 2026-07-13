package com.dev.org.mapper;

import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.model.NotificationResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class NotificationResponseMapper {

    public NotificationResponse toResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setActionUrl(notification.getActionUrl());
        response.setType(notification.getType());
        response.setPriority(
                mapEnum(
                        notification.getPriority(),
                        value -> NotificationResponse.PriorityEnum.fromValue(value.name())));
        response.setAudienceType(
                mapEnum(
                        notification.getAudienceType(),
                        value -> NotificationResponse.AudienceTypeEnum.fromValue(value.name())));
        response.setSeverity(
                mapEnum(
                        notification.getSeverity(),
                        value -> NotificationResponse.SeverityEnum.fromValue(value.name())));
        response.setStatus(mapEnum(notification.getStatus(), this::mapStatus));
        response.setTargets(notification.getTargets() != null ? notification.getTargets() : null);
        response.setExpiresAt(
                notification.getExpiresAt() != null
                        ? OffsetDateTime.ofInstant(notification.getExpiresAt(), ZoneOffset.UTC)
                        : null);
        response.setCreatedAt(
                notification.getCreatedAt() != null
                        ? OffsetDateTime.ofInstant(notification.getCreatedAt(), ZoneOffset.UTC)
                        : null);
        response.setUpdatedAt(
                notification.getUpdatedAt() != null
                        ? OffsetDateTime.ofInstant(notification.getUpdatedAt(), ZoneOffset.UTC)
                        : null);
        return response;
    }

    private <E extends Enum<E>, R> R mapEnum(E value, Function<E, R> mapper) {
        if (value == null) {
            return null;
        }
        try {
            return mapper.apply(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private NotificationResponse.StatusEnum mapStatus(NotificationStatus status) {
        return NotificationResponse.StatusEnum.fromValue(status.name());
    }
}
