package com.pida

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PidaApplication

fun main(args: Array<String>) {
	runApplication<PidaApplication>(*args)
}
