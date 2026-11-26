package com.github.jactor.rises.persistence.test.extensions

import assertk.assertThat
import assertk.assertions.isFailure
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class AssertExtensionsTest {
    @Test
    fun `should not be older than 60 seconds`() {
        val fiftyNineSecondsAgo = LocalDateTime.now().minusSeconds(59)
        assertThat(fiftyNineSecondsAgo).isNotOlderThan(seconds = 60)
    }

    @Test
    fun `should be older than 60 seconds`() {
        val fiftyNineSecondsAgo = LocalDateTime.now().minusSeconds(61)
        val result = runCatching {
            assertThat(fiftyNineSecondsAgo).isNotOlderThan(seconds = 60)
        }

        assertThat(result).isFailure()
    }
}
