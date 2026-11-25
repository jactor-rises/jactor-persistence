package com.github.jactor.rises.persistence.user

import com.github.jactor.rises.persistence.util.toUserDao
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update
import java.util.UUID

object UserRepositoryObject : UserRepository {
    override suspend fun contains(username: String): Boolean = Users
        .select(column = Users.username)
        .andWhere { Users.username eq username }
        .count() > 0

    override suspend fun findById(id: UUID): UserDao? = Users
        .selectAll()
        .andWhere { Users.id eq id }
        .singleOrNull()
        ?.toUserDao()

    override suspend fun findByPersonId(id: UUID): List<UserDao> = Users
        .selectAll()
        .andWhere { Users.personId eq id }
        .toList()
        .map { it.toUserDao() }

    override suspend fun findByUsername(username: String): UserDao? = Users
        .selectAll()
        .andWhere { Users.username eq username }
        .singleOrNull()
        ?.toUserDao()

    override suspend fun findUsernames(userRole: List<UserRole>): List<String> = userRole.map { it.name }.let { types ->
        Users.select(column = Users.username)
            .andWhere { Users.userType inList types }
            .withDistinct()
            .toList()
            .map { it[Users.username] }
    }

    override suspend fun save(userDao: UserDao): UserDao = when (userDao.isPersisted) {
        true -> update(user = userDao)
        false -> insert(user = userDao)
    }

    private suspend fun insert(user: UserDao): UserDao = Users.insertAndGetId { row ->
        row[createdBy] = user.createdBy
        row[modifiedBy] = user.modifiedBy
        row[timeOfCreation] = user.timeOfCreation
        row[timeOfModification] = user.timeOfModification
        row[emailAddress] = user.emailAddress
        row[username] = user.username
        row[personId] = requireNotNull(user.personId) { "Person id cannot be null when inserting a user." }
        row[userType] = user.userRole.name
    }.value
        .let { newId -> user.also { it.id = newId } }

    private suspend fun update(user: UserDao): UserDao = Users.update(
        where = { Users.id eq user.id },
    ) {
        it[modifiedBy] = user.modifiedBy
        it[timeOfModification] = user.timeOfModification
        it[emailAddress] = user.emailAddress
        it[username] = user.username
        it[userType] = user.userRole.name
        it[personId] = requireNotNull(user.personId) { "Person id cannot be null when updating a user." }
        // createdBy & timeOfCreation are intentionally not updated
    }.let { user }
}
