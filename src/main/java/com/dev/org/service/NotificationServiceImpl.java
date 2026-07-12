package com.dev.org.service;

import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.mapper.NotificationEntityMapper;
import com.dev.org.repository.NotificationRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationEntityMapper notificationEntityMapper;
    private final NotificationPublisher notificationPublisher;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationEntityMapper notificationEntityMapper,
            NotificationPublisher notificationPublisher) {
        this.notificationRepository = notificationRepository;
        this.notificationEntityMapper = notificationEntityMapper;
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public Mono<Notification> createNotification(Notification notification) {
        return Mono.fromSupplier(() -> prepareForCreate(notification))
                .map(notificationEntityMapper::toEntity)
                .flatMap(notificationRepository::save)
                .map(notificationEntityMapper::toDomain)
                .flatMap(notificationPublisher::publish);
    }

    private Notification prepareForCreate(Notification notification) {
        Instant now = Instant.now();
        notification.setStatus(NotificationStatus.ACTIVE);
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        if (notification.getExpiresAt() == null) {
            notification.setExpiresAt(now.plusSeconds(15L * 24 * 60 * 60));
        }
        return notification;
    }
}
