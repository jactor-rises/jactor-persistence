package com.github.jactor.rises.persistence.test

import io.r2dbc.spi.Connection
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryMetadata
import io.r2dbc.spi.ConnectionFactoryOptions
import org.reactivestreams.Publisher
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import reactor.core.publisher.Mono
import javax.sql.DataSource

@TestConfiguration
class R2dbcEmbeddedDatabaseTestConfig {
    @Bean
    @Primary
    fun r2dbcConnectionFactory(dataSource: DataSource): ConnectionFactory {
        return object : ConnectionFactory {
            override fun create(): Publisher<out Connection> = Mono.defer {
                val connectionMetadata = dataSource.connection.use { connection ->
                    connection.metaData.run { url to userName }
                }
                val connectionFactoryOptions = ConnectionFactoryOptions.parse(
                    connectionMetadata.first.replaceFirst("jdbc:", "r2dbc:"),
                ).mutate()
                    .option(ConnectionFactoryOptions.USER, connectionMetadata.second)
                    .build()

                Mono.from(ConnectionFactories.get(connectionFactoryOptions).create())
            }

            override fun getMetadata(): ConnectionFactoryMetadata =
                ConnectionFactoryMetadata { "Zonky embedded PostgreSQL" }
        }
    }
}
