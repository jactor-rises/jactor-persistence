package com.github.jactor.rises.persistence

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.Test

class UsernameContextTest {
    @Test
    suspend fun `should get username from context when present`() {
        /** this is to simulate the UsernameWebFilter as it is reading from the current context */
        val username = mono { UsernameContext.currentUsername().awaitSingle() }
            .contextWrite { context -> UsernameContext.withUsername(context = context, username = "jactor") }
            .awaitSingle()

        assertThat(username).isEqualTo("jactor")
    }

    @Test
    suspend fun `should fail when getting username from context when empty`() {
        val result = runCatching { UsernameContext.fetchUsernameOrFail() }

        assertThat(result).isFailure().hasMessage("No username in current reactor context!")
    }
}
