package com.github.jactor.rises.persistence.admin

import com.github.jactor.rises.persistence.config.SecurityConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class AuthenticationService(
    @Value($$"${app.security.jactor-password-hash}") private val jactorPasswordHash: String,
    private val passwordEncoder: PasswordEncoder,
    private val jwtEncoder: JwtEncoder,
) {
    init {
        require(SecurityConfig.BCryptHash.matches(jactorPasswordHash)) {
            "JACTOR_PASSWORD_HASH must contain a BCrypt password hash"
        }
    }

    suspend fun authenticate(
        username: String,
        password: String,
    ): String? = withContext(Dispatchers.Default) {
        val passwordMatches = passwordEncoder.matches(password, jactorPasswordHash)

        if (username != JACTOR_USERNAME || !passwordMatches) {
            null
        } else {
            createToken()
        }
    }

    private fun createToken(): String {
        val issuedAt = Instant.now()
        val claims = JwtClaimsSet.builder()
            .subject(JACTOR_USERNAME)
            .issuedAt(issuedAt)
            .expiresAt(issuedAt.plus(TOKEN_LIFETIME))
            .build()
        val header = JwsHeader.with(MacAlgorithm.HS256).build()

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
    }

    private companion object {
        const val JACTOR_USERNAME = "jactor"
        val TOKEN_LIFETIME: Duration = Duration.ofMinutes(15)
    }
}
