package com.dev.org.controller;

import com.dev.org.mapper.NotificationRequestMapper;
import com.dev.org.mapper.NotificationResponseMapper;
import com.dev.org.model.CreateNotificationRequest;
import com.dev.org.model.NotificationResponse;
import com.dev.org.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRequestMapper notificationRequestMapper;
    private final NotificationResponseMapper notificationResponseMapper;

    public NotificationController(
            NotificationService notificationService,
            NotificationRequestMapper notificationRequestMapper,
            NotificationResponseMapper notificationResponseMapper) {
        this.notificationService = notificationService;
        this.notificationRequestMapper = notificationRequestMapper;
        this.notificationResponseMapper = notificationResponseMapper;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<NotificationResponse>> createNotification(
            @RequestBody Mono<CreateNotificationRequest> createNotificationRequest) {
        return createNotificationRequest
                .map(notificationRequestMapper::toDomain)
                .flatMap(notificationService::createNotification)
                .map(notificationResponseMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
}
