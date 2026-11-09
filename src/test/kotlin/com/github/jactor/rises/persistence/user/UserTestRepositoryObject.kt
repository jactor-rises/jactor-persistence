package com.github.jactor.rises.persistence.user

import com.github.jactor.rises.persistence.util.toUserDao
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

object UserTestRepositoryObject {
    suspend fun delete(user: UserDao) {
        suspendTransaction {
            Users.deleteWhere { Users.id eq user.id }
        }
    }

    suspend fun findAll(): List<UserDao> = suspendTransaction {
        Users.selectAll().toList().map { it.toUserDao() }
    }
}
