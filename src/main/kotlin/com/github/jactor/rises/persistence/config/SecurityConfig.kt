package com.github.jactor.rises.persistence.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http
        .csrf { it.disable() }
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .authorizeExchange { authorize ->
            authorize.pathMatchers(HttpMethod.POST, "/admin/login").permitAll()
            authorize.pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            authorize.anyExchange().authenticated()
        }
        .oauth2ResourceServer { oauth2 -> oauth2.jwt { } }
        .build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtEncoder(
        @Value($$"${app.security.jwt-secret}") jwtSecret: String,
    ): JwtEncoder = NimbusJwtEncoder(ImmutableSecret(secretKey(jwtSecret)))

    @Bean
    fun reactiveJwtDecoder(
        @Value($$"${app.security.jwt-secret}") jwtSecret: String,
    ): ReactiveJwtDecoder = NimbusReactiveJwtDecoder
        .withSecretKey(secretKey(jwtSecret))
        .macAlgorithm(MacAlgorithm.HS256)
        .build()

    private fun secretKey(jwtSecret: String): SecretKey {
        val secretBytes = jwtSecret.toByteArray(StandardCharsets.UTF_8)
        require(secretBytes.size >= MINIMUM_SECRET_BYTES) {
            "JWT_SECRET must be at least $MINIMUM_SECRET_BYTES UTF-8 bytes"
        }

        return SecretKeySpec(secretBytes, "HmacSHA256")
    }

    companion object {
        private const val MINIMUM_SECRET_BYTES = 32
        val BCryptHash = """^\$2[aby]\$\d{2}\$[./A-Za-z0-9]{53}$""".toRegex()
    }
}
