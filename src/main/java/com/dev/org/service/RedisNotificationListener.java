package com.dev.org.service;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.User;
import com.dev.org.event.NotificationEvent;
import com.dev.org.mapper.NotificationEventMapper;
import java.util.List;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RedisNotificationListener implements NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(RedisNotificationListener.class);

    private final Flux<Notification> notificationFlux;

    public RedisNotificationListener(
            ReactiveRedisConnectionFactory connectionFactory,
            NotificationEventMapper notificationEventMapper,
            ChannelTopic notificationChannelTopic,
            RedisSerializationContext.SerializationPair<String> notificationChannelSerializationPair,
            RedisSerializationContext.SerializationPair<NotificationEvent>
                    notificationEventSerializationPair) {

        this.notificationFlux = Flux.defer(
                        () -> Flux.usingWhen(
                                Mono.fromSupplier(
                                        () -> new ReactiveRedisMessageListenerContainer(
                                                connectionFactory)),
                                listenerContainer -> listenerContainer.receive(
                                                List.of(notificationChannelTopic),
                                                notificationChannelSerializationPair,
                                                notificationEventSerializationPair)
                                        .map(ReactiveSubscription.Message::getMessage)
                                        .map(notificationEventMapper::toDomain),
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
        return !Collections.disjoint(roles, targets);
    }
}
