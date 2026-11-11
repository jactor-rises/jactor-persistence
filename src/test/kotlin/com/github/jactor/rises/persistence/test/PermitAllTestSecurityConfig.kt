package com.github.jactor.rises.persistence.test

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@TestConfiguration
@EnableWebFluxSecurity
class PermitAllTestSecurityConfig {
    @Bean
    fun permitAllTestSecurityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http
        .csrf { it.disable() }
        .authorizeExchange { authorize -> authorize.anyExchange().permitAll() }
        .build()
}
