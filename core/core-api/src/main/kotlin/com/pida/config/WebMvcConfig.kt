package com.pida.config

import com.pida.support.filter.UserArgumentResolver
import com.pida.user.UserService
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val userService: UserService,
) : WebMvcConfigurer {
    companion object {
        const val ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH"
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods(*ALLOWED_METHOD_NAMES.split(",").toTypedArray())
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(UserArgumentResolver(userService))
    }
}
