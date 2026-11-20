package com.github.jactor.rises.persistence.admin

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import com.github.jactor.rises.persistence.test.RandomUsernameProvider
import com.github.jactor.rises.persistence.user.UserRepositoryObject
import com.github.jactor.rises.persistence.user.Users
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.statements.StatementType
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AdminServiceBeanTest @Autowired constructor(
    private val adminService: AdminService,
) : AbstractSpringBootNoDirtyContextTest() {
    @Test
    suspend fun `should move user from users to inactive users`() {
        "deadbeat".let { username ->
            createUser(username = username)

            assertThat(
                UserRepositoryObject.findByUsername(username = username)?.let { true } ?: false,
                "before",
            ).isTrue()

            val movedToInactive = adminService.setUserInactive(username = username)

            assertThat(UserRepositoryObject.contains(username = username), "after").isFalse()
            assertThat(movedToInactive, "moved").isTrue()

            val tracingUsername = requireNotNull(RandomUsernameProvider.usernameHolder.get())
            val inactiveUserIsComplete = suspendTransaction {
                exec(
                    """
                    SELECT EXISTS (
                        SELECT 1
                        FROM T_USER_INACTIVE
                        WHERE USERNAME = ?
                          AND MODIFIED_BY = ?
                    ) AS complete;
                    """.trimIndent(),
                    args = listOf(
                        Users.username.columnType to username,
                        Users.modifiedBy.columnType to tracingUsername,
                    ),
                    explicitStatementType = StatementType.SELECT,
                ) { row ->
                    row.get("complete", Boolean::class.javaObjectType)
                }?.singleOrNull() ?: false
            }

            assertThat(inactiveUserIsComplete, "inactive user").isTrue()
        }
    }
}
