package com.dev.org.service;

import com.dev.org.domain.Notification;
import reactor.core.publisher.Mono;

public interface NotificationPublisher {
    Mono<Notification> publish(Notification notification);
}
