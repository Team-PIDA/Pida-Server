package com.pida.swagger

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class StaticRoutingConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/")
        registry
            .addResourceHandler("/favicon.ico")
            .addResourceLocations("classpath:/static/")
        registry
            .addResourceHandler("/js/**")
            .addResourceLocations("/js/")
        registry
            .addResourceHandler("/css/**")
            .addResourceLocations("/css/")
        registry
            .addResourceHandler("/images/**")
            .addResourceLocations("/images/")
    }
}
