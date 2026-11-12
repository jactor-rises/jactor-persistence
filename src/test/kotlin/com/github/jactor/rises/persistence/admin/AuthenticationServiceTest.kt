package com.github.jactor.rises.persistence.admin

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.github.jactor.rises.persistence.config.SecurityConfig
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assumptions.assumeThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import java.time.Instant
import java.time.temporal.ChronoUnit

internal class AuthenticationServiceTest {
    @Nested
    inner class AuthenticationServiceJunitTest {
        private val passwordEncoder = BCryptPasswordEncoder()
        private val storedPasswordHash = requireNotNull(passwordEncoder.encode("correct-password"))
        private val jwtEncoderMockk = mockk<JwtEncoder> {
            every { encode(any()) } returns Jwt.withTokenValue("signed-token")
                .header("alg", "HS256")
                .subject("jactor")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .build()
        }
        private val authenticationService = AuthenticationServiceBean(
            jactorPasswordHash = storedPasswordHash,
            passwordEncoder = passwordEncoder,
            jwtEncoder = jwtEncoderMockk,
        )

        @Test
        fun `should issue a token for jactor with the correct password`() = runTest {
            val token = authenticationService.authenticate(username = "jactor", password = "correct-password")

            assertThat(token?.tokenValue ?: "missing token").isEqualTo("signed-token")
        }

        @Test
        fun `should reject a different username`() = runTest {
            val token = authenticationService.authenticate(username = "someone-else", password = "correct-password")

            assertThat(token).isNull()
        }

        @Test
        fun `should reject an incorrect password`() = runTest {
            val token = authenticationService.authenticate(username = "jactor", password = "incorrect-password")

            assertThat(token).isNull()
        }
    }

    @Nested
    inner class AuthenticationServiceModelIntegrationClass {
        private val jactorPassword = "very-secret"
        private val jwtSecretLabel = "JWT_SECRET"
        private val securityConfig = SecurityConfig()
        private lateinit var authenticationService: AuthenticationService

        @BeforeEach
        fun `init service when environment has existing jwt-secret and password hash`() {
            val envMap = System.getenv()
            assumeThat(envMap.keys.contains(jwtSecretLabel)).isTrue()

            authenticationService = AuthenticationServiceTestBean(
                jactorPasswordHash = securityConfig.passwordEncoder().encode(jactorPassword) ?: fail { "???" },
                jwtEncoder = securityConfig.jwtEncoder(jwtSecret = fetchExistingVariable(label = jwtSecretLabel)),
                passwordEncoder = securityConfig.passwordEncoder(),
            )
        }

        private fun fetchExistingVariable(envMap: Map<String, String> = System.getenv(), label: String): String {
            return envMap.getOrElse(key = label) { fail { "Missing environment variable $label" } }
        }

        @Test
        suspend fun `should authorize a valid token with correct password`() {
            val jwtToken = authenticationService.authenticate(
                username = "jactor",
                password = jactorPassword,
            ) ?: fail { "Authentication failed???" }

            val encodingResult = runCatching {
                securityConfig.reactiveJwtDecoder(
                    jwtSecret = fetchExistingVariable(label = jwtSecretLabel),
                ).decode(jwtToken.tokenValue).awaitSingle()
            }

            assertThat(encodingResult.isSuccess).isTrue()
        }

        @Test
        suspend fun `should have an invalid token when the token is expired`() {
            (authenticationService as AuthenticationServiceTestBean).issuedAt(
                instant = Instant.now().minus(30, ChronoUnit.MINUTES),
            )

            val jwtToken = authenticationService.authenticate(
                username = "jactor",
                password = jactorPassword,
            ) ?: fail { "Authentication failed???" }

            val encodingResult = runCatching {
                securityConfig.reactiveJwtDecoder(
                    jwtSecret = fetchExistingVariable(label = jwtSecretLabel),
                ).decode(jwtToken.tokenValue).awaitSingle()
            }

            assertThat(encodingResult.isFailure).isTrue()
        }
    }

    private class AuthenticationServiceTestBean(
        jactorPasswordHash: String,
        jwtEncoder: JwtEncoder,
        passwordEncoder: PasswordEncoder,
    ) : AuthenticationServiceBean(
            jactorPasswordHash = jactorPasswordHash,
            jwtEncoder = jwtEncoder,
            passwordEncoder = passwordEncoder,
        ) {
        fun issuedAt(instant: Instant) {
            super.issuedTimestamp = { instant }
        }
    }
}
