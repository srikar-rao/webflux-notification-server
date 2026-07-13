package com.dev.org.service;

import com.dev.org.domain.User;
import com.dev.org.model.UserNotificationQueryDto;
import com.dev.org.repository.NotificationQueryRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationQueryRepository notificationQueryRepository;

    public Flux<UserNotificationQueryDto> getUnreadNotifications(String userId, Set<String> roles) {
        User user = User.builder().id(userId).roles(roles != null ? roles : Set.of()).build();
        return notificationQueryRepository.findUnreadNotifications(user);
    }
}
