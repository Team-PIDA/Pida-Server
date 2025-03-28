package com.pida.storage.db.core.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class CoreDataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "datasource.db.core")
    fun coreHikariConfig(): HikariConfig = HikariConfig()

    @Bean
    fun coreDataSource(
        @Qualifier("coreHikariConfig") config: HikariConfig,
    ): HikariDataSource = HikariDataSource(config)
}
