package com.dev.org.mapper;

import com.dev.org.domain.Notification;
import com.dev.org.model.NotificationResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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
        response.setPriority(NotificationResponse.PriorityEnum.fromValue(notification.getPriority().name()));
        response.setAudienceType(
                NotificationResponse.AudienceTypeEnum.fromValue(notification.getAudienceType().name()));
        response.setSeverity(NotificationResponse.SeverityEnum.fromValue(notification.getSeverity().name()));
        response.setStatus(NotificationResponse.StatusEnum.fromValue(notification.getStatus().name()));
        response.setTargets(
                notification.getTargets() != null ? new ArrayList<>(notification.getTargets()) : null);
        response.setExpiresAt(
                notification.getExpiresAt() != null
                        ? OffsetDateTime.ofInstant(notification.getExpiresAt(), ZoneOffset.UTC)
                        : null);
        response.setCreatedAt(OffsetDateTime.ofInstant(notification.getCreatedAt(), ZoneOffset.UTC));
        response.setUpdatedAt(OffsetDateTime.ofInstant(notification.getUpdatedAt(), ZoneOffset.UTC));
        return response;
    }
}
