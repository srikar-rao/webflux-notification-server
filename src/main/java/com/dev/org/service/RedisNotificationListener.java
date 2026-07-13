package com.dev.org.service;

import com.dev.org.domain.Notification;
import com.dev.org.domain.User;
import com.dev.org.mapper.NotificationResponseMapper;
import com.dev.org.model.NotificationResponse;
import com.dev.org.model.NotificationResponse.AudienceTypeEnum;
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

    private final Flux<NotificationResponse> notificationFlux;

    public RedisNotificationListener(
            ReactiveRedisConnectionFactory connectionFactory,
            NotificationResponseMapper notificationResponseMapper,
            ChannelTopic notificationChannelTopic,
            RedisSerializationContext.SerializationPair<String>
                    notificationChannelSerializationPair,
            RedisSerializationContext.SerializationPair<Notification>
                    notificationSerializationPair) {

        List<ChannelTopic> topics = List.of(notificationChannelTopic);
        ReactiveRedisMessageListenerContainer listenerContainer =
                new ReactiveRedisMessageListenerContainer(connectionFactory);
        RedisSerializationContext.SerializationPair<String> channelPair =
                notificationChannelSerializationPair;
        RedisSerializationContext.SerializationPair<Notification> notificationPair =
                notificationSerializationPair;
        NotificationResponseMapper responseMapper = notificationResponseMapper;

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
                                                                        notificationPair)
                                                                .map(
                                                                        ReactiveSubscription.Message
                                                                                ::getMessage)
                                                                .map(responseMapper::toResponse),
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
                .filter(notificationResponse -> shouldDeliver(notificationResponse, user))
                .onBackpressureLatest();
    }

    private boolean shouldDeliver(NotificationResponse notificationResponse, User user) {
        if (notificationResponse.getAudienceType() == AudienceTypeEnum.GLOBAL) {
            return true;
        }

        Set<String> targets =
                notificationResponse.getTargets() == null
                        ? Collections.emptySet()
                        : notificationResponse.getTargets();
        if (notificationResponse.getAudienceType() == AudienceTypeEnum.USER) {
            return user.getId() != null && targets.contains(user.getId());
        }

        Set<String> roles = user.getRoles() == null ? Collections.emptySet() : user.getRoles();
        return !Collections.disjoint(roles, targets);
    }
}
