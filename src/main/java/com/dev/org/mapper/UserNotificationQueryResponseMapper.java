package com.dev.org.mapper;

import com.dev.org.model.UserNotificationQueryDto;
import com.dev.org.model.UserNotificationQueryResponse;
import java.time.ZoneOffset;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class UserNotificationQueryResponseMapper {

    public UserNotificationQueryResponse toResponse(UserNotificationQueryDto dto) {
        if (dto == null) {
            return null;
        }

        UserNotificationQueryResponse response = new UserNotificationQueryResponse();
        response.setId(dto.id());
        response.setTitle(dto.title());
        response.setMessage(dto.message());
        response.setActionUrl(dto.actionUrl());
        response.setType(dto.type());

        if (dto.priority() != null) {
            response.setPriority(
                    UserNotificationQueryResponse.PriorityEnum.fromValue(dto.priority().name()));
        }

        if (dto.audienceType() != null) {
            response.setAudienceType(
                    UserNotificationQueryResponse.AudienceTypeEnum.fromValue(
                            dto.audienceType().name()));
        }

        if (dto.severity() != null) {
            response.setSeverity(
                    UserNotificationQueryResponse.SeverityEnum.fromValue(dto.severity().name()));
        }

        if (dto.status() != null) {
            response.setStatus(
                    UserNotificationQueryResponse.StatusEnum.fromValue(dto.status().name()));
        }

        if (dto.targets() != null) {
            response.setTargets(new java.util.HashSet<>(Arrays.asList(dto.targets())));
        }

        if (dto.expiresAt() != null) {
            response.setExpiresAt(dto.expiresAt().atOffset(ZoneOffset.UTC));
        }

        if (dto.createdAt() != null) {
            response.setCreatedAt(dto.createdAt().atOffset(ZoneOffset.UTC));
        }

        if (dto.updatedAt() != null) {
            response.setUpdatedAt(dto.updatedAt().atOffset(ZoneOffset.UTC));
        }

        response.setStateId(dto.stateId());
        response.setUserId(dto.userId());

        if (dto.readAt() != null) {
            response.setReadAt(dto.readAt().atOffset(ZoneOffset.UTC));
        }

        if (dto.dismissedAt() != null) {
            response.setDismissedAt(dto.dismissedAt().atOffset(ZoneOffset.UTC));
        }

        return response;
    }
}
