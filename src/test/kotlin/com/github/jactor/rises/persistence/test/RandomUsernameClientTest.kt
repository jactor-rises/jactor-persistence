package com.github.jactor.rises.persistence.test

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Isolated
import org.springframework.web.client.RestClient

@Isolated
class RandomUsernameClientTest {
    private val randomUsernameClient = RandomUsernameClient(
        restClientBuilder = RestClient.builder(),
        baseUrl = "https://randomuser.me",
    )

    @Test
    fun `should generate a different username for every usage`() {
        val firstUsername = randomUsernameClient.fetchUsernames()
        val secondUsername = randomUsernameClient.fetchUsernames()

        assertAll {
            assertThat(firstUsername, "first").isNotEmpty()
            assertThat(secondUsername, "second").isNotEmpty()
            assertThat(secondUsername, "both").isNotEqualTo(firstUsername)
        }
    }
}
