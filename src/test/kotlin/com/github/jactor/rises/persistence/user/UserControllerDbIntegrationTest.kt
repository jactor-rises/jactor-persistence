package com.github.jactor.rises.persistence.user

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import com.github.jactor.rises.persistence.test.assertAll
import com.github.jactor.rises.persistence.test.createToken
import com.github.jactor.rises.persistence.test.initCreateUserCommand
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.test.web.reactive.server.WebTestClient

class UserControllerDbIntegrationTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val jwtEncoder: JwtEncoder,
    private val webTestClient: WebTestClient,
) : AbstractSpringBootNoDirtyContextTest() {
    @Test
    suspend fun `should create an user with creator`() {
        val createdUsername = "shitface"
        val createdBy = "creator"
        val authorizationHeader = "Bearer ${jwtEncoder.createToken(createdBy)}"

        webTestClient.post()
            .uri("/user")
            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
            .bodyValue(initCreateUserCommand(username = createdUsername, surname = "Peanuts"))
            .exchange()
            .expectStatus()
            .isCreated

        val userDao = userRepository.findByUsername(createdUsername)

        assertThat(actual = userDao).isNotNull().assertAll { dao ->
            assertThat(actual = dao.username, name = "username").isEqualTo(createdUsername)
            assertThat(actual = dao.toPersistent().createdBy, name = "createdBy").isEqualTo(createdBy)
            assertThat(actual = dao.toPersistent().modifiedBy, name = "modifiedBy").isEqualTo(createdBy)
        }
    }
}
