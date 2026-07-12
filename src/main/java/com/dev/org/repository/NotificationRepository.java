package com.dev.org.repository;

import com.dev.org.entity.NotificationEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<NotificationEntity, Long> {}
