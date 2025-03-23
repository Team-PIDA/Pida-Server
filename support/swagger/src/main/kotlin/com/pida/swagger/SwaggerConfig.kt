package com.pida.swagger

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.core.jackson.ModelResolver
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalTime
import java.time.YearMonth

@Configuration
internal class SwaggerConfig(
    private val swaggerProperties: SwaggerProperties,
) {
    @Bean
    fun openAPI(): OpenAPI {
        SpringDocUtils
            .getConfig()
            .replaceWithSchema(LocalTime::class.java, StringSchema().example("HH:mm:ss.SSS").nullable(true))
            .replaceWithSchema(YearMonth::class.java, StringSchema().example("yyyy-MM").nullable(true))

        return OpenAPI()
            .info(swaggerInfo())
            .servers(
                listOf(swaggerProperties.domain)
                    .map { Server().url(it) },
            ).components(authComponents())
    }

    @Bean
    fun modelResolver(objectMapper: ObjectMapper): ModelResolver = ModelResolver(objectMapper)

    private fun swaggerInfo(): Info {
        val license =
            License()
                .url("https://github.com/Team-PIDA/Pida-Server")
                .name("Pida API Server")
        return Info()
            .title("\uD83C\uDF6D Pida API Server")
            .description("Pida API 문서입니다.")
            .version("v0.0.1")
            .license(license)
    }

    private fun authComponents() =
        Components()
            .addSecuritySchemes(
                "accessToken",
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .`in`(SecurityScheme.In.HEADER)
                    .name("Authorization"),
            )
}
