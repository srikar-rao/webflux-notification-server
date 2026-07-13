package com.dev.org.controller;

import com.dev.org.mapper.UserNotificationQueryResponseMapper;
import com.dev.org.model.UserNotificationQueryResponse;
import com.dev.org.service.NotificationQueryService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/user-notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final NotificationQueryService notificationQueryService;
    private final UserNotificationQueryResponseMapper mapper;

    @GetMapping
    public Flux<UserNotificationQueryResponse> findUnreadUserNotifications(
            @RequestParam String userId, @RequestParam(required = false) Set<String> roles) {
        return notificationQueryService
                .getUnreadNotifications(userId, roles != null ? roles : Set.of())
                .map(mapper::toResponse);
    }
}
