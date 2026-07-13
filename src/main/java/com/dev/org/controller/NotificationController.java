package com.dev.org.controller;

import com.dev.org.controller.api.NotificationsApi;
import com.dev.org.domain.User;
import com.dev.org.mapper.NotificationRequestMapper;
import com.dev.org.mapper.NotificationResponseMapper;
import com.dev.org.model.CreateNotificationRequest;
import com.dev.org.model.NotificationResponse;
import com.dev.org.model.NotificationStreamRequest;
import com.dev.org.service.NotificationListener;
import com.dev.org.service.NotificationService;
import jakarta.validation.Valid;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class NotificationController implements NotificationsApi {

    private final NotificationService notificationService;
    private final NotificationRequestMapper notificationRequestMapper;
    private final NotificationResponseMapper notificationResponseMapper;
    private final NotificationListener notificationListener;

    public NotificationController(
            NotificationService notificationService,
            NotificationRequestMapper notificationRequestMapper,
            NotificationResponseMapper notificationResponseMapper,
            NotificationListener notificationListener) {
        this.notificationService = notificationService;
        this.notificationRequestMapper = notificationRequestMapper;
        this.notificationResponseMapper = notificationResponseMapper;
        this.notificationListener = notificationListener;
    }

    @Override
    public Mono<ResponseEntity<NotificationResponse>> createNotification(
            @Valid @RequestBody Mono<CreateNotificationRequest> createNotificationRequest,
            ServerWebExchange exchange) {
        return createNotificationRequest
                .map(notificationRequestMapper::toDomain)
                .flatMap(notificationService::createNotification)
                .map(notificationResponseMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Override
    public Mono<ResponseEntity<Flux<NotificationResponse>>> streamNotifications(
            @Valid @RequestBody Mono<NotificationStreamRequest> notificationStreamRequest,
            ServerWebExchange exchange) {
        return notificationStreamRequest
                .map(
                        request -> {
                            User user =
                                    User.builder()
                                            .id(request.getId())
                                            .roles(toLinkedSet(request.getRoles()))
                                            .build();
                            return notificationListener.listen(user);
                        })
                .map(ResponseEntity::ok);
    }

    private Set<String> toLinkedSet(Set<String> values) {
        return values != null ? new LinkedHashSet<>(values) : null;
    }
}
