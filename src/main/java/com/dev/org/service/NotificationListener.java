package com.dev.org.service;

import com.dev.org.domain.Notification;
import com.dev.org.domain.User;
import reactor.core.publisher.Flux;

public interface NotificationListener {
    Flux<Notification> listen(User user);
}
