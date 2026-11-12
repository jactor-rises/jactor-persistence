package com.github.jactor.rises.persistence.admin

import assertk.assertThat
import assertk.assertions.startsWith
import com.github.jactor.rises.persistence.config.SecurityConfig
import com.github.jactor.rises.persistence.test.createToken
import com.github.jactor.rises.persistence.user.UserController
import com.github.jactor.rises.persistence.user.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.coVerify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(controllers = [AdminController::class, UserController::class])
@Import(SecurityConfig::class, AuthenticationServiceBean::class)
@TestPropertySource(properties = ["app.security.jwt-secret=0123456789abcdef0123456789abcdef"])
internal class SecurityConfigTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val jwtEncoder: JwtEncoder,
    @MockkBean private val adminServiceMockk: AdminService,
    @MockkBean private val userServiceMockk: UserService,
) {
    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun registerAuthenticationProperties(registry: DynamicPropertyRegistry) {
            registry.add("app.security.jactor-password-hash") {
                requireNotNull(BCryptPasswordEncoder().encode("secret"))
            }
        }
    }

    @Test
    fun `should return bearer token after successful login`() {
        val authorizationHeader = webTestClient
            .post()
            .uri("/admin/login")
            .bodyValue(LoginRequest(username = "jactor", password = "secret"))
            .exchange()
            .expectStatus()
            .isOk
            .returnResult()
            .responseHeaders
            .getFirst(HttpHeaders.AUTHORIZATION)
            ?: error("Login did not return an Authorization header")

        assertThat(authorizationHeader).startsWith("Bearer ")
        coEvery { userServiceMockk.find(username = "jactor") } returns null

        webTestClient
            .get()
            .uri("/user/name/jactor")
            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    fun `should reject login with incorrect credentials`() {
        webTestClient
            .post()
            .uri("/admin/login")
            .bodyValue(LoginRequest(username = "jactor", password = "incorrect"))
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `should reject requests without a token`() {
        webTestClient
            .get()
            .uri("/user/name/jactor")
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `should accept a valid bearer token`() {
        coEvery { userServiceMockk.find(username = "jactor") } returns null

        webTestClient
            .get()
            .uri("/user/name/jactor")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${jwtEncoder.createToken()}")
            .exchange()
            .expectStatus()
            .isNoContent

        coVerify(exactly = 1) { userServiceMockk.find(username = "jactor") }
    }
}
