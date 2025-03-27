package com.pida.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pida.jwt.JwtConverter
import com.pida.swagger.SwaggerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val swaggerProperties: SwaggerProperties,
) {
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
    @Order(1)
    fun swaggerFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher(getSwaggerUrls())
            .httpBasic { }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().authenticated()
            }

        return http.build()
    }

    fun getSwaggerUrls(): RequestMatcher =
        OrRequestMatcher(
            AntPathRequestMatcher("/swagger-ui/**"),
            AntPathRequestMatcher("/v3/api-docs/**"),
            AntPathRequestMatcher("/swagger-resources/**"),
        )

    @Bean
    fun inMemoryUserDetailsManager(): InMemoryUserDetailsManager {
        val user: UserDetails =
            User
                .withUsername(swaggerProperties.user)
                .password(passwordEncoder().encode(swaggerProperties.password))
                .roles("SWAGGER")
                .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtConverter: JwtConverter,
    ): SecurityFilterChain {
        http
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(jwtConverter.apply { setPrincipalClaimName("jti") })
                }
                oauth2.authenticationEntryPoint(CustomAuthenticationEntryPoint(objectMapper()))
            }

        http
            .headers { it.frameOptions { option -> option.disable() } }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling { it.authenticationEntryPoint(CustomAuthenticationEntryPoint(objectMapper())) }

        http.httpBasic { it.realmName("Swagger Realm") }

        http.authorizeHttpRequests { authorize ->
            // Swagger 인증
            authorize.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").hasRole("SWAGGER")

            // 인증 없이 허용할 API
            authorize
                .requestMatchers(
                    "/api/v1/flower-spot/**",
                    "/api/v1/auth/**",
                    "/api/v1/flower-spot",
                    "/api/v1/auth",
                    "/api/v1/blooming/{spotId}/details",
                ).permitAll()

            // 추가로 열어줄 API
            authorize.requestMatchers("/h2-console/**", "/actuator/**", "/ping").permitAll()

            // 그 외 모든 API는 JWT 인증 필요
            authorize.requestMatchers("/api/v1/user/**", "/api/v1/blooming").authenticated()

            // 나머지도 다 인증
            authorize.anyRequest().authenticated()
        }

        return http.build()
    }
}
