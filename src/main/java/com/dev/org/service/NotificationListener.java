package com.dev.org.service;

import com.dev.org.domain.User;
import com.dev.org.model.NotificationResponse;
import reactor.core.publisher.Flux;

public interface NotificationListener {
    Flux<NotificationResponse> listen(User user);
}
