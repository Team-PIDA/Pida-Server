package com.pida

import org.junit.jupiter.api.Tag
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestConstructor

@Tag("integration")
@SpringBootTest
@ConfigurationPropertiesScan
@ContextConfiguration(
    classes = [
        MySQLTestContainersConfig::class,
        RedisContainersConfig::class,
        LocalStackAwsConfig::class,
    ],
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
annotation class IntegrationTest
