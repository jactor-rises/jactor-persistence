package com.github.jactor.rises.persistence.admin

import com.github.jactor.rises.persistence.ContextUsernameProvider
import com.github.jactor.rises.persistence.UsernameProvider
import com.github.jactor.rises.persistence.user.Users
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.statements.StatementType
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.springframework.stereotype.Repository

interface AdminRepository {
    suspend fun setUserInactive(username: String): Boolean
}

@Repository
class AdminRepositoryBean(
    private val usernameProvider: UsernameProvider = ContextUsernameProvider(),
) : AdminRepository {
    override suspend fun setUserInactive(username: String): Boolean {
        val modifiedBy = usernameProvider.fetchUsername()

        return suspendTransaction {
            exec(
                """
                WITH source_user AS MATERIALIZED (
                    SELECT ID, CREATED_BY, CREATION_TIME, EMAIL, USERNAME, PERSON_ID, USER_ROLE
                    FROM T_USER
                    WHERE USERNAME = ?
                    FOR UPDATE
                ),
                inserted_user AS (
                    INSERT INTO T_USER_INACTIVE (
                        ID, CREATED_BY, CREATION_TIME, MODIFIED_BY, MODIFICATION_TIME,
                        EMAIL, USERNAME, PERSON_ID, USER_ROLE, INACTIVE_SINCE
                    )
                    SELECT ID, CREATED_BY, CREATION_TIME, ?, now(),
                           EMAIL, USERNAME, PERSON_ID, USER_ROLE, now()
                    FROM source_user
                    RETURNING ID
                ),
                deleted_user AS (
                    DELETE FROM T_USER
                    WHERE ID IN (SELECT ID FROM inserted_user)
                )
                SELECT EXISTS (SELECT 1 FROM inserted_user) AS moved;
                """.trimIndent(),
                args = listOf(
                    Users.username.columnType to username,
                    Users.modifiedBy.columnType to modifiedBy,
                ),
                explicitStatementType = StatementType.SELECT,
            ) { row ->
                row.get("moved", Boolean::class.javaObjectType)
            }?.singleOrNull() ?: error("The move query did not return a result")
        }
    }
}
