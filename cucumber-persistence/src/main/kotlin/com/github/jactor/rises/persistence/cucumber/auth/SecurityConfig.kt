package com.github.jactor.rises.persistence.cucumber.auth

import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
class SecurityConfig {

    @Bean
    fun jwtDecoder(
        @Value($$"${app.security.jwt-secret}") jwtSecret: String,
    ): ReactiveJwtDecoder = NimbusReactiveJwtDecoder
        .withSecretKey(secretKey(jwtSecret))
        .macAlgorithm(MacAlgorithm.HS256)
        .build()

    @Bean
    fun jwtEncoder(
        @Value($$"${app.security.jwt-secret}") jwtSecret: String,
    ): JwtEncoder = NimbusJwtEncoder(ImmutableSecret(secretKey(jwtSecret)))

    private fun secretKey(jwtSecret: String): SecretKey {
        val secretBytes = jwtSecret.toByteArray(StandardCharsets.UTF_8)

        require(secretBytes.size >= MINIMUM_SECRET_BYTES) {
            "JWT_SECRET must be at least $MINIMUM_SECRET_BYTES UTF-8 bytes"
        }

        return SecretKeySpec(secretBytes, "HmacSHA256")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    companion object {
        private const val MINIMUM_SECRET_BYTES = 32
        val BCryptHash = """^\$2[aby]\$\d{2}\$[./A-Za-z0-9]{53}$""".toRegex()
    }
}
