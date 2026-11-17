package com.github.jactor.rises.persistence

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
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
        val firstUsername = filterWebExchange(webExchange = initWebExchange(username = "first-user"))
        val secondUsername = filterWebExchange(webExchange = initWebExchange(username = "second-user"))
        val usernameWithoutPrincipal = filterWebExchange(webExchange = initWebExchageWithoutToken())

        assertAll {
            assertThat(actual = firstUsername, name = "first").isEqualTo("first-user")
            assertThat(actual = secondUsername, name = "second").isEqualTo("second-user")
            assertThat(actual = usernameWithoutPrincipal, name = "without").isNull()
        }
    }

    private fun filterWebExchange(webExchange: ServerWebExchange): String? {
        var downstreamUsername: String? = null
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

    private fun initWebExchageWithoutToken(): ServerWebExchange =
        MockServerWebExchange.from(MockServerHttpRequest.get("/").build())
}
