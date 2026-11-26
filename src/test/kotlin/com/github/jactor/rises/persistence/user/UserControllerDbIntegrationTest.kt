package com.github.jactor.rises.persistence.user

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.github.jactor.rises.persistence.config.RandomUsernameTestConfig
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
    suspend fun `should create an user tracked by usernameProvider`() {
        val createdUsername = "shitface"
        val authorizationHeader = "Bearer ${jwtEncoder.createToken("someone")}"

        webTestClient.post()
            .uri("/user")
            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
            .bodyValue(initCreateUserCommand(username = createdUsername, surname = "Peanuts"))
            .exchange()
            .expectStatus()
            .isCreated

        val userDao = userRepository.findByUsername(createdUsername)

        assertThat(actual = userDao).isNotNull().assertAll { dao ->
            val persistent = dao.toPersistent()
            val (createdBy, modifiedBy) = run { persistent.createdBy to persistent.modifiedBy }

            assertThat(actual = dao.username, name = "username").isEqualTo(createdUsername)
            assertThat(actual = RandomUsernameTestConfig.usernames, name = "createdBy").contains(createdBy)
            assertThat(actual = RandomUsernameTestConfig.usernames, name = "modifiedBy").contains(modifiedBy)
        }
    }
}
