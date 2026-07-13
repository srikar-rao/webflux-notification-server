package com.dev.org.service;

import com.dev.org.domain.Notification;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RedisNotificationPublisher implements NotificationPublisher {

    private final ReactiveRedisTemplate<String, Notification> reactiveNotificationRedisTemplate;
    private final ChannelTopic notificationChannelTopic;

    public RedisNotificationPublisher(
            ReactiveRedisTemplate<String, Notification> reactiveNotificationRedisTemplate,
            ChannelTopic notificationChannelTopic) {
        this.reactiveNotificationRedisTemplate = reactiveNotificationRedisTemplate;
        this.notificationChannelTopic = notificationChannelTopic;
    }

    @Override
    public Mono<Notification> publish(Notification notification) {
        return reactiveNotificationRedisTemplate
                .convertAndSend(notificationChannelTopic.getTopic(), notification)
                .thenReturn(notification);
    }
}
