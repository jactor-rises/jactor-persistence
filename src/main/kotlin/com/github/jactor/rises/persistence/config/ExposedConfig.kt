package com.github.jactor.rises.persistence.config

import io.r2dbc.spi.ConnectionFactory
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabaseConfig
import org.jetbrains.exposed.v1.r2dbc.transactions.TransactionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(info = Info(title = "jactor-persistence.exposed", version = "v2"))
class ExposedConfig {
    @Bean
    fun databaseConfig() = R2dbcDatabaseConfig.Companion {
        useNestedTransactions = true
        explicitDialect = PostgreSQLDialect()
    }

    @Bean
    fun database(
        connectionFactory: ConnectionFactory,
        databaseConfig: R2dbcDatabaseConfig.Builder,
    ): R2dbcDatabase = R2dbcDatabase.connect(connectionFactory, databaseConfig).also {
        TransactionManager.defaultDatabase = it
    }
}
