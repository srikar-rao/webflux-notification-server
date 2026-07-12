package com.dev.org.service;

import com.dev.org.domain.AudienceType;
import com.dev.org.model.NotificationResponse;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDelivery {
    private AudienceType audienceType;
    private Set<String> targets;
    private NotificationResponse response;
}
