package com.github.jactor.rises.persistence.admin

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import java.time.Instant

internal class AuthenticationServiceTest {
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
    private val authenticationService = AuthenticationService(
        jactorPasswordHash = storedPasswordHash,
        passwordEncoder = passwordEncoder,
        jwtEncoder = jwtEncoderMockk,
    )

    @Test
    fun `should issue a token for jactor with the correct password`() = runTest {
        val token = authenticationService.authenticate(username = "jactor", password = "correct-password")

        assertThat(token ?: "missing token").isEqualTo("signed-token")
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
