package com.pida

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.context.ImportTestcontainers

@TestConfiguration(proxyBeanMethods = false)
@ImportTestcontainers(value = [MySQLTestContainer::class])
class MySQLTestContainersConfig
