package com.github.jactor.rises.persistence.test

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
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
    fun `should fetch a different username for every usage`() {
        val firstUsername = randomUsernameClient.fetchUsernames().firstOrNull()
        val secondUsername = randomUsernameClient.fetchUsernames().firstOrNull()

        assertAll {
            assertThat(firstUsername, "first").isNotNull().isNotEmpty()
            assertThat(secondUsername, "second").isNotNull().isNotEmpty()
            assertThat(secondUsername, "both").isNotEqualTo(firstUsername)
        }
    }

    @Test
    fun `should fetch 10 usernames`() {
        val usernames = randomUsernameClient.fetchUsernames(total = 10)

        assertThat(usernames).hasSize(10)
    }

    @Test
    suspend fun `should fail fetching username from list of usernames when it has not been supplied`() {
        val result = runCatching { randomUsernameClient.fetchUsername() }

        assertThat(result).isFailure().given {
            assertThat(it).isInstanceOf(NoSuchElementException::class.java)
        }
    }

    @Test
    suspend fun `should fetch a random username from supplied list of usernames`() {
        randomUsernameClient.fetchUsernames(total = 1)
        val username = randomUsernameClient.fetchUsername()

        assertThat(username).isNotEmpty()
    }
}
