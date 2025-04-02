package com.pida

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestConstructor

@Tag("develop")
@SpringBootTest
@ContextConfiguration(classes = [RedisContainersConfig::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
annotation class DevelopTest
