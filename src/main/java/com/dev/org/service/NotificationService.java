package com.dev.org.service;

import com.dev.org.domain.Notification;
import reactor.core.publisher.Mono;

public interface NotificationService {
    Mono<Notification> createNotification(Notification notification);
}
