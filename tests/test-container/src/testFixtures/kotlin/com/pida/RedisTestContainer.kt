package com.pida

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
interface RedisTestContainer {
    companion object {
        @Container
        @JvmStatic
        val redis: GenericContainer<*> =
            GenericContainer("redis:7.2.1-alpine")
                .withExposedPorts(6379)
                .withReuse(true)
                .apply { start() }

        @DynamicPropertySource
        @JvmStatic
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("core.storage.redis.host") { redis.host }
            registry.add("auth.storage.redis.host") { redis.host }
            registry.add("core.storage.redis.port") { redis.getMappedPort(6379).toString() }
            registry.add("auth.storage.redis.port") { redis.getMappedPort(6379).toString() }
        }
    }
}
