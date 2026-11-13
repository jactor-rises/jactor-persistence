package com.github.jactor.rises.persistence.admin

import com.github.jactor.rises.persistence.UUIDv7
import com.github.jactor.rises.persistence.config.SecurityConfig
import com.github.jactor.rises.shared.whenTrue
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
import java.util.UUID

interface AuthenticationService {
    suspend fun authenticate(username: String, password: String): JwtToken?
}

@Service
class AuthenticationServiceBean(
    @Value($$"${app.security.jactor-password-hash}") private val jactorPasswordHash: String,
    private val passwordEncoder: PasswordEncoder,
    private val jwtEncoder: JwtEncoder,
    protected var issuedTimestamp: () -> Instant = { Instant.now() },
) : AuthenticationService {
    init {
        require(SecurityConfig.BCryptHash.matches(jactorPasswordHash)) {
            "JACTOR_PASSWORD_HASH must contain a BCrypt password hash"
        }
    }

    override suspend fun authenticate(
        username: String,
        password: String,
    ): JwtToken? = withContext(Dispatchers.Default) {
        when (username) {
            JACTOR_USERNAME -> {
                val passwordMatches = passwordEncoder.matches(password, jactorPasswordHash)

                passwordMatches.whenTrue {
                    val tokenId = UUIDv7.generate()
                    JwtToken(
                        id = tokenId,
                        tokenValue = createToken(),
                    )
                }
            }

            else -> {
                null
            }
        }
    }

    private fun createToken(): String {
        val issuedAt = issuedTimestamp.invoke()
        val claims = JwtClaimsSet.builder()
            .subject(JACTOR_USERNAME)
            .issuedAt(issuedAt)
            .expiresAt(issuedAt.plus(TOKEN_LIFETIME_15_MINUTES))
            .build()
        val header = JwsHeader.with(MacAlgorithm.HS256).build()

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
    }

    private companion object {
        const val JACTOR_USERNAME = "jactor"
        val TOKEN_LIFETIME_15_MINUTES: Duration = Duration.ofMinutes(15)
    }
}

@JvmRecord
data class JwtToken(val id: UUID, val tokenValue: String) {
    fun createBearerToken(): String = "Bearer $tokenValue"
}
