package com.dev.org.controller;

import com.dev.org.domain.User;
import com.dev.org.mapper.NotificationResponseMapper;
import com.dev.org.model.NotificationResponse;
import com.dev.org.model.NotificationStreamRequest;
import com.dev.org.service.NotificationListener;
import java.util.LinkedHashSet;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/notifications")
public class NotificationStreamController {

    private final NotificationListener notificationListener;
    private final NotificationResponseMapper notificationResponseMapper;

    public NotificationStreamController(
            NotificationListener notificationListener,
            NotificationResponseMapper notificationResponseMapper) {
        this.notificationListener = notificationListener;
        this.notificationResponseMapper = notificationResponseMapper;
    }

    @PostMapping(path = "/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationResponse>> streamNotifications(
            @RequestBody NotificationStreamRequest request) {
        User user = User.builder()
                .id(request.getId())
                .roles(request.getRoles() != null ? new LinkedHashSet<>(request.getRoles()) : null)
                .build();

        return notificationListener.listen(user)
                .map(notificationResponseMapper::toResponse)
                .map(
                        notification ->
                                ServerSentEvent.<NotificationResponse>builder()
                                        .event("notification")
                                        .data(notification)
                                        .build());
    }
}
