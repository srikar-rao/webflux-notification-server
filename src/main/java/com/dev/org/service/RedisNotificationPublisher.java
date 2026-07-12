package com.dev.org.service;

import com.dev.org.domain.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RedisNotificationPublisher implements NotificationPublisher {

    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final String channelName;

    public RedisNotificationPublisher(
            ReactiveStringRedisTemplate reactiveStringRedisTemplate,
            ObjectMapper objectMapper,
            @Value("${app.redis.notification-channel}") String channelName) {
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        this.objectMapper = objectMapper;
        this.channelName = channelName;
    }

    @Override
    public Mono<Notification> publish(Notification notification) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(notification))
                .flatMap(payload -> reactiveStringRedisTemplate.convertAndSend(channelName, payload))
                .thenReturn(notification)
                .onErrorMap(
                        JsonProcessingException.class,
                        ex -> new IllegalStateException("Unable to serialize notification for Redis", ex));
    }
}
