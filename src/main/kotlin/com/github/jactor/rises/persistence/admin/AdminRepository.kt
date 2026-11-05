package com.github.jactor.rises.persistence.admin

import com.github.jactor.rises.persistence.user.Users
import org.jetbrains.exposed.v1.core.statements.StatementType
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.springframework.stereotype.Repository

interface AdminRepository {
    suspend fun moveInactiveUser(username: String): Boolean
}

@Repository
class AdminRepositoryBean : AdminRepository {
    override suspend fun moveInactiveUser(username: String): Boolean {
        return suspendTransaction {
            exec(
                """
                WITH source_user AS MATERIALIZED (
                    SELECT ID, CREATED_BY, CREATION_TIME, UPDATED_BY, UPDATED_TIME,
                           EMAIL, USER_NAME, PERSON_ID, USER_TYPE
                    FROM T_USER
                    WHERE USER_NAME = ?
                    FOR UPDATE
                ),
                inserted_user AS (
                    INSERT INTO T_USER_INACTIVE (
                        ID, CREATED_BY, CREATION_TIME, UPDATED_BY, UPDATED_TIME,
                        EMAIL, USER_NAME, PERSON_ID, USER_TYPE, INACTIVE_SINCE
                    )
                    SELECT ID, CREATED_BY, CREATION_TIME, UPDATED_BY, UPDATED_TIME,
                           EMAIL, USER_NAME, PERSON_ID, USER_TYPE, now()
                    FROM source_user
                    RETURNING ID
                ),
                deleted_user AS (
                    DELETE FROM T_USER
                    WHERE ID IN (SELECT ID FROM inserted_user)
                )
                SELECT EXISTS (SELECT 1 FROM inserted_user) AS moved;
                """.trimIndent(),
                args = listOf(Users.username.columnType to username),
                explicitStatementType = StatementType.SELECT,
            ) { resultSet ->
                check(resultSet.next()) { "Expected to find user data " }
                resultSet.getBoolean("moved")
            } ?: error("The move query did not return a result")
        }
    }
}
