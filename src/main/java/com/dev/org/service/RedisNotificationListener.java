package com.dev.org.service;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RedisNotificationListener implements NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(RedisNotificationListener.class);

    private final Flux<Notification> notificationFlux;

    public RedisNotificationListener(
            ReactiveRedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper,
            @Value("${app.redis.notification-channel}") String channelName) {
        this.notificationFlux = Flux.defer(
                        () -> Flux.usingWhen(
                                Mono.fromSupplier(
                                        () -> new ReactiveRedisMessageListenerContainer(
                                                connectionFactory)),
                                listenerContainer -> listenerContainer.receive(new ChannelTopic(channelName))
                                        .map(ReactiveSubscription.Message::getMessage)
                                        .flatMap(payload -> deserialize(objectMapper, payload)),
                                ReactiveRedisMessageListenerContainer::destroyLater))
                .doOnError(error -> log.error("Redis notification listener stopped", error))
                .onErrorResume(error -> Flux.empty())
                .publish()
                .refCount(1);
    }

    @Override
    public Flux<Notification> listen(User user) {
        return notificationFlux
                .filter(notification -> shouldDeliver(notification, user))
                .onBackpressureLatest();
    }

    private Mono<Notification> deserialize(ObjectMapper objectMapper, String payload) {
        return Mono.fromCallable(() -> objectMapper.readValue(payload, Notification.class))
                .onErrorResume(
                        JsonProcessingException.class,
                        ex -> {
                            log.error("Unable to deserialize Redis notification payload", ex);
                            return Mono.empty();
                        });
    }

    private boolean shouldDeliver(Notification notification, User user) {
        if (notification.getAudienceType() == AudienceType.GLOBAL) {
            return true;
        }

        Set<String> targets =
                notification.getTargets() == null ? Collections.emptySet() : notification.getTargets();
        if (notification.getAudienceType() == AudienceType.USER) {
            return user.getId() != null && targets.contains(user.getId());
        }

        Set<String> roles = user.getRoles() == null ? Collections.emptySet() : user.getRoles();
        return roles.stream().anyMatch(targets::contains);
    }
}
