package com.pida

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class PidaApplication

fun main(args: Array<String>) {
    runApplication<PidaApplication>(*args)
}
