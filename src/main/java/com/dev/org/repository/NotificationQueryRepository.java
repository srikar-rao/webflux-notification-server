package com.dev.org.repository;

import com.dev.org.domain.User;
import com.dev.org.model.UserNotificationQueryDto;
import reactor.core.publisher.Flux;

public interface NotificationQueryRepository {
    Flux<UserNotificationQueryDto> findUnreadNotifications(User user);
}
