package com.dev.org.repository;

import com.dev.org.entity.UserNotificationStateEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationStateRepository
        extends ReactiveCrudRepository<UserNotificationStateEntity, Long> {}
