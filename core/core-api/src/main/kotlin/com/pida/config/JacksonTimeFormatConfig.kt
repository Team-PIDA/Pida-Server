package com.pida.config

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Configuration
class JacksonTimeFormatConfig {
    @Bean
    fun javaTimeModule(): JavaTimeModule { // To ensure that LocalDateTime always includes millisecond
        return JavaTimeModule().apply {
            addSerializer(
                LocalDateTime::class.java,
                LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")),
            )
            addSerializer(
                ZonedDateTime::class.java,
                ZonedDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")),
            )
            addSerializer(
                LocalTime::class.java,
                LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
            )
            addSerializer(
                YearMonth::class.java,
                YearMonthSerializer(DateTimeFormatter.ofPattern("yyyy-MM")),
            )
            addSerializer(
                LocalTime::class.java,
                LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
            )
        }
    }
}
