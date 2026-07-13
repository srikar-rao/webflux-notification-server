package com.dev.org.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource(
        value = "classpath:sql/notification-queries-sql.yml",
        factory = YamlPropertySourceFactory.class)
public class TodoSqlUtil {

    @Value("${get-unread-notifications}")
    private String getUnreadNotificationsQuery;
}
