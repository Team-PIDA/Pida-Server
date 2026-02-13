package com.pida.notification

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Component
class NotificationStoredKeyGenerator {
    companion object {
        private val FORMAT_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

    fun generate(): String = "${generateDate()}_NHK_${generateUUID()}"

    private fun generateUUID(): String = UUID.randomUUID().toString().replace("-", "")

    private fun generateDate(): String = FORMAT_YYYYMMDD.format(LocalDate.now())
}
