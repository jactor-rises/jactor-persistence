package com.github.jactor.rises.persistence.admin

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import com.github.jactor.rises.persistence.user.UserRepositoryObject
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AdminServiceBeanTest @Autowired constructor(
    private val adminService: AdminService,
) : AbstractSpringBootNoDirtyContextTest() {
    @Test
    suspend fun `should move user from users to inactive users`() {
        createUser(username = USERNAME_DEADBEAT)

        assertThat(
            UserRepositoryObject.findByUsername(username = USERNAME_DEADBEAT)?.let { true } ?: false,
            "before",
        ).isTrue()

        adminService.moveInactiveUser(username = USERNAME_DEADBEAT)

        assertThat(
            UserRepositoryObject.findByUsername(username = USERNAME_DEADBEAT)?.let { true } ?: false,
            "after",
        ).isFalse()
    }

    companion object {
        private const val USERNAME_DEADBEAT = "deadbeat"
    }
}
