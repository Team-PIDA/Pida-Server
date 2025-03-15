package com.pida.authentication.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pida.authentication.jwt.JwtConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper().registerModules(
            JavaTimeModule().apply {
                addSerializer(
                    LocalDate::class.java,
                    LocalDateSerializer(DateTimeFormatter.ISO_DATE),
                )
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
            },
        )

    @Bean
    fun grantedAuthorityDefaults(): GrantedAuthorityDefaults = GrantedAuthorityDefaults("")

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtConverter: JwtConverter,
    ): SecurityFilterChain {
        http
            .oauth2ResourceServer { oauth2ResourceServer ->
                oauth2ResourceServer.jwt { jwtConfig ->
                    jwtConfig.jwtAuthenticationConverter(
                        jwtConverter.apply {
                            setPrincipalClaimName("jti")
                        },
                    )
                }
                oauth2ResourceServer.authenticationEntryPoint(
                    AuthenticationEntryPoint(
                        objectMapper(),
                    ),
                )
            }
        http
            .headers {
                it.frameOptions { option ->
                    option.disable()
                }
            }.csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorizeHttpRequest ->
                authorizeHttpRequest.requestMatchers("/swagger-ui/**").permitAll()
                authorizeHttpRequest.requestMatchers("/v3/api-docs/**").permitAll()
                authorizeHttpRequest.requestMatchers("/h2-console/**").permitAll()
                authorizeHttpRequest.requestMatchers("/actuator/**").permitAll()
                authorizeHttpRequest.requestMatchers("/ping").permitAll()

                authorizeHttpRequest.anyRequest().authenticated()
            }.exceptionHandling {
                it.authenticationEntryPoint(
                    AuthenticationEntryPoint(
                        objectMapper(),
                    ),
                )
            }

        return http.build()
    }
}
