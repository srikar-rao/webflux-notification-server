package com.dev.org;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("integration")
@SpringBootTest(properties = "management.health.redis.enabled=false")
class ApplicationTests {

    @Test
    void contextLoads() {}
}
