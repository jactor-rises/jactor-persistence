package com.github.jactor.rises.persistence.cucumber

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.github.jactor.rises.persistence.cucumber.auth.AuthenticationService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig(CucumberSpringConfiguration::class)
class CucumberSpringConfigurationTest {
    @Autowired
    private lateinit var authenticationService: AuthenticationService

    @Autowired
    private lateinit var jwtDecoder: ReactiveJwtDecoder

    @Test
    fun `creates a token from the Spring context that the decoder accepts`() {
        val jwtToken = runBlocking {
            authenticationService.authenticate(username = "jactor", password = JACTOR_PASSWORD)
        }

        val decodedJwt = jwtDecoder.decode(requireNotNull(jwtToken).tokenValue).block()

        assertThat(decodedJwt?.subject).isEqualTo("jactor")
    }

    private companion object {
        private const val JACTOR_PASSWORD = "cucumber-context-test-password"
        private const val JWT_SECRET = "0123456789abcdef0123456789abcdef"
        private val JACTOR_PASSWORD_HASH = requireNotNull(BCryptPasswordEncoder().encode(JACTOR_PASSWORD))

        @JvmStatic
        @DynamicPropertySource
        fun registerAuthenticationProperties(registry: DynamicPropertyRegistry) {
            registry.add("app.security.jwt-secret") { JWT_SECRET }
            registry.add("app.security.jactor-password-hash") { JACTOR_PASSWORD_HASH }
        }
    }
}
