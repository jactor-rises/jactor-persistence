package com.github.jactor.rises.persistence.cucumber.auth

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.util.UUID

class JwtTokenTest {
    @Test
    fun `creates a bearer authorization header from the token value`() {
        val jwtToken = JwtToken(id = UUID.randomUUID(), tokenValue = "signed-token")

        assertThat(jwtToken.createBearerToken()).isEqualTo("Bearer signed-token")
    }
}
