package com.dev.org.config;

import com.dev.org.event.NotificationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public ChannelTopic notificationChannelTopic(
            @Value("${app.redis.notification-channel}") String channelName) {
        return new ChannelTopic(channelName);
    }

    @Bean
    public RedisSerializationContext.SerializationPair<String>
            notificationChannelSerializationPair() {
        return RedisSerializationContext.SerializationPair.fromSerializer(
                new StringRedisSerializer());
    }

    @Bean
    public RedisSerializationContext.SerializationPair<NotificationEvent>
            notificationEventSerializationPair(ObjectMapper objectMapper) {
        return RedisSerializationContext.SerializationPair.fromSerializer(
                new Jackson2JsonRedisSerializer<>(objectMapper.copy(), NotificationEvent.class));
    }

    @Bean
    public ReactiveRedisTemplate<String, NotificationEvent> reactiveNotificationRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory,
            RedisSerializationContext.SerializationPair<NotificationEvent>
                    notificationEventSerializationPair) {

        RedisSerializationContext<String, NotificationEvent> serializationContext =
                RedisSerializationContext.<String, NotificationEvent>newSerializationContext(
                                new StringRedisSerializer())
                        .key(new StringRedisSerializer())
                        .value(notificationEventSerializationPair)
                        .hashKey(new StringRedisSerializer())
                        .hashValue(notificationEventSerializationPair)
                        .build();
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}
