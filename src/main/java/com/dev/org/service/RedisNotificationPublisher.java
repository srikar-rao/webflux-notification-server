package com.dev.org.service;

import com.dev.org.domain.Notification;
import com.dev.org.event.NotificationEvent;
import com.dev.org.mapper.NotificationEventMapper;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RedisNotificationPublisher implements NotificationPublisher {

    private final ReactiveRedisTemplate<String, NotificationEvent>
            reactiveNotificationRedisTemplate;
    private final NotificationEventMapper notificationEventMapper;
    private final ChannelTopic notificationChannelTopic;

    public RedisNotificationPublisher(
            ReactiveRedisTemplate<String, NotificationEvent> reactiveNotificationRedisTemplate,
            NotificationEventMapper notificationEventMapper,
            ChannelTopic notificationChannelTopic) {
        this.reactiveNotificationRedisTemplate = reactiveNotificationRedisTemplate;
        this.notificationEventMapper = notificationEventMapper;
        this.notificationChannelTopic = notificationChannelTopic;
    }

    @Override
    public Mono<Notification> publish(Notification notification) {
        NotificationEvent notificationEvent = notificationEventMapper.toEvent(notification);
        return reactiveNotificationRedisTemplate
                .convertAndSend(notificationChannelTopic.getTopic(), notificationEvent)
                .thenReturn(notification);
    }
}
