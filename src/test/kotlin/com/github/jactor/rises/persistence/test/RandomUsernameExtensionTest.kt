package com.github.jactor.rises.persistence.test

import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.parallel.Isolated
import org.junit.jupiter.api.parallel.ResourceLock
import java.util.UUID

@Isolated
@ExtendWith(RandomUsernameExtension::class)
@ResourceLock(RandomUsernameExtension.RESOURCE_LOCK)
class RandomUsernameExtensionTest {
    @Test
    suspend fun `should generate a different username for consecutive test executions`() {
        val extension = RandomUsernameExtension()
        val context = mockk<ExtensionContext>()

        extension.beforeTestExecution(context)
        val firstUsername = RandomUsernameProvider().fetchUsername()
        extension.afterTestExecution(context)

        extension.beforeTestExecution(context)
        val secondUsername = RandomUsernameProvider().fetchUsername()
        extension.afterTestExecution(context)

        assertThat(firstUsername).isNotEmpty()
        assertThat(runCatching { UUID.fromString(firstUsername) }.getOrNull()).isNotNull()
        assertThat(secondUsername).isNotEqualTo(firstUsername)
    }

    @Test
    suspend fun `should clear the username after test execution`() {
        val extension = RandomUsernameExtension()
        val context = mockk<ExtensionContext>()

        extension.beforeTestExecution(context)
        extension.afterTestExecution(context)

        assertThat(runCatching { RandomUsernameProvider().fetchUsername() }).isFailure()
    }
}
