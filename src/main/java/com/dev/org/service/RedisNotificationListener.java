package com.dev.org.service;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.User;
import com.dev.org.event.NotificationEvent;
import com.dev.org.mapper.NotificationEventMapper;
import com.dev.org.mapper.NotificationResponseMapper;
import com.dev.org.model.NotificationResponse;
import java.util.Collections;
import java.util.List;
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

    private final Flux<NotificationDelivery> notificationFlux;

    public RedisNotificationListener(
            ReactiveRedisConnectionFactory connectionFactory,
            NotificationEventMapper notificationEventMapper,
            NotificationResponseMapper notificationResponseMapper,
            ChannelTopic notificationChannelTopic,
            RedisSerializationContext.SerializationPair<String>
                    notificationChannelSerializationPair,
            RedisSerializationContext.SerializationPair<NotificationEvent>
                    notificationEventSerializationPair) {

        List<ChannelTopic> topics = List.of(notificationChannelTopic);
        ReactiveRedisMessageListenerContainer listenerContainer =
                new ReactiveRedisMessageListenerContainer(connectionFactory);
        RedisSerializationContext.SerializationPair<String> channelPair =
                notificationChannelSerializationPair;
        RedisSerializationContext.SerializationPair<NotificationEvent> eventPair =
                notificationEventSerializationPair;
        NotificationResponseMapper responseMapper = notificationResponseMapper;
        java.util.function.Function<Notification, NotificationDelivery> deliveryMapper =
                notification -> toDelivery(notification, responseMapper);

        this.notificationFlux =
                Flux.defer(
                                () ->
                                        Flux.usingWhen(
                                                Mono.fromSupplier(() -> listenerContainer),
                                                container ->
                                                        container
                                                                .receive(
                                                                        topics,
                                                                        channelPair,
                                                                        eventPair)
                                                                .map(
                                                                        ReactiveSubscription.Message
                                                                                ::getMessage)
                                                                .map(
                                                                        notificationEventMapper
                                                                                ::toDomain)
                                                                .map(deliveryMapper),
                                                ReactiveRedisMessageListenerContainer
                                                        ::destroyLater))
                        .doOnError(error -> log.error("Redis notification listener stopped", error))
                        .onErrorResume(error -> Flux.empty())
                        .publish()
                        .refCount(1);
    }

    @Override
    public Flux<NotificationResponse> listen(User user) {
        return notificationFlux
                .filter(notificationDelivery -> shouldDeliver(notificationDelivery, user))
                .map(NotificationDelivery::getResponse)
                .onBackpressureLatest();
    }

    private NotificationDelivery toDelivery(
            Notification notification, NotificationResponseMapper notificationResponseMapper) {
        return NotificationDelivery.builder()
                .audienceType(notification.getAudienceType())
                .targets(notification.getTargets())
                .response(notificationResponseMapper.toResponse(notification))
                .build();
    }

    private boolean shouldDeliver(NotificationDelivery notificationDelivery, User user) {
        if (notificationDelivery.getAudienceType() == AudienceType.GLOBAL) {
            return true;
        }

        Set<String> targets =
                notificationDelivery.getTargets() == null
                        ? Collections.emptySet()
                        : notificationDelivery.getTargets();
        if (notificationDelivery.getAudienceType() == AudienceType.USER) {
            return user.getId() != null && targets.contains(user.getId());
        }

        Set<String> roles = user.getRoles() == null ? Collections.emptySet() : user.getRoles();
        return !Collections.disjoint(roles, targets);
    }
}
