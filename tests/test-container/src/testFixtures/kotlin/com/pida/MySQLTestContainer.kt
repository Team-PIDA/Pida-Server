package com.pida

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

interface MySQLTestContainer {
    companion object {
        private const val VERSION = "mysql:8.0.36"

        @Container
        @JvmStatic
        var mySqlContainer: MySQLContainer<*> =
            MySQLContainer<Nothing>(DockerImageName.parse(VERSION))
                .apply { start() }

        @DynamicPropertySource
        @JvmStatic
        fun jdbcProperties(registry: DynamicPropertyRegistry) {
            registry.add("datasource.db.core.driver-class-name") { mySqlContainer.driverClassName }
            registry.add("datasource.db.core.jdbc-url") { mySqlContainer.jdbcUrl }
            registry.add("datasource.db.core.username") { mySqlContainer.username }
            registry.add("datasource.db.core.password") { mySqlContainer.password }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create" }
        }
    }
}
