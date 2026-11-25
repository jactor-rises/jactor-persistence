package com.github.jactor.rises.persistence.admin

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import com.github.jactor.rises.persistence.user.InactiveUsers
import com.github.jactor.rises.persistence.user.UserRepository
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AdminServiceBeanTest @Autowired constructor(
    private val adminService: AdminService,
    private val userRepository: UserRepository,
) : AbstractSpringBootNoDirtyContextTest() {
    @Test
    suspend fun `should move user from users to inactive users`() {
        "deadbeat".let { username ->
            createUser(username = username)

            assertThat(
                userRepository.findByUsername(username = username)?.let { true } ?: false,
                "before",
            ).isTrue()

            val movedToInactive = adminService.setUserInactive(username = username)

            assertThat(userRepository.contains(username = username), "after").isFalse()
            assertThat(movedToInactive, "moved").isTrue()

            val inactiveUser = suspendTransaction {
                InactiveUsers
                    .selectAll()
                    .where { InactiveUsers.username eq username }
                    .orderBy(
                        InactiveUsers.timeOfModification to SortOrder.DESC,
                        InactiveUsers.id to SortOrder.DESC,
                    )
                    .limit(1)
                    .singleOrNull()
                    ?.get(InactiveUsers.username)
            }

            assertThat(inactiveUser, "inactive user").isEqualTo(username)
        }
    }
}
