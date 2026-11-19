package com.github.jactor.rises.persistence

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isSuccess
import org.junit.jupiter.api.Test
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import java.time.Instant

class UsernameWebFilterTest {
    @Test
    fun `should propagate authenticated username and isolates it between requests`() {
        val firstUsername = fetchUsernameFromReactorContext(webExchange = initWebExchange(username = "first-user"))
        val secondUsername = fetchUsernameFromReactorContext(webExchange = initWebExchange(username = "second-user"))

        assertAll {
            assertThat(actual = firstUsername, name = "first").isEqualTo("first-user")
            assertThat(actual = secondUsername, name = "second").isEqualTo("second-user")
        }
    }

    @Test
    fun `should not fail when authentication principal is missing`() {
        val result = runCatching {
            fetchUsernameFromReactorContext(webExchange = initWebExchangeWithoutToken())
        }

        assertThat(result).isSuccess()
    }

    @Test
    fun `should fail when authentication name is blank`() {
        val result = runCatching {
            fetchUsernameFromReactorContext(webExchange = initWebExchange(username = " "))
        }

        assertThat(result)
            .isFailure()
            .hasMessage("The username is blank, no authentication name could be gathered")
    }

    private fun fetchUsernameFromReactorContext(webExchange: ServerWebExchange): String? {
        var downstreamUsername: String? = null

        // UsernameContext returns Mono from which Username is gathered, but this is not executed before the Mono is
        // blocked when the UsernameWebFilter is returned...
        val chain = WebFilterChain {
            UsernameContext.currentUsername()
                .doOnNext { username -> downstreamUsername = username }
                .then()
        }

        UsernameWebFilter().filter(webExchange, chain).block()

        return downstreamUsername
    }

    private fun initWebExchange(username: String): ServerWebExchange {
        val issuedAt = Instant.now()
        val jwt = Jwt.withTokenValue("verified-token")
            .header("alg", "HS256")
            .subject(username)
            .issuedAt(issuedAt)
            .expiresAt(issuedAt.plusSeconds(900))
            .build()

        return MockServerWebExchange.builder(MockServerHttpRequest.get("/").build())
            .principal(JwtAuthenticationToken(jwt))
            .build()
    }

    private fun initWebExchangeWithoutToken(): ServerWebExchange =
        MockServerWebExchange.from(MockServerHttpRequest.get("/").build())
}
