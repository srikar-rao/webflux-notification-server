package com.dev.org.repository;

import com.dev.org.domain.User;
import com.dev.org.model.UserNotificationQueryDto;
import com.dev.org.util.TodoSqlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final DatabaseClient databaseClient;
    private final TodoSqlUtil sqlUtil;

    @Override
    public Flux<UserNotificationQueryDto> findUnreadNotifications(User user) {
        return databaseClient
                .sql(sqlUtil.getGetUnreadNotificationsQuery())
                .bind("userId", user.getId())
                .bind("userRoles", user.getRoles().toArray(new String[0]))
                .mapProperties(UserNotificationQueryDto.class)
                .all();
    }
}
