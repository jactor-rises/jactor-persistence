package com.github.jactor.rises.persistence

import com.github.jactor.rises.shared.whenTrue
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.springframework.stereotype.Component

typealias Modifier<T> = suspend (T) -> T

interface PersistenceHandler {
    suspend fun <T> read(fetch: suspend () -> T): T = fetch.invoke()
    suspend fun <T : PersistentDao<*>> save(dao: T, modifier: Modifier<T>): T = saveOrUpdate(
        dao,
        modifier,
        usernameProvider = NotAuthenticatedUser.usernameProvider,
    )
}

object NotAuthenticatedUser {
    var usernameProvider: UsernameProvider = { "anonymous" }
}

suspend fun <T : PersistentDao<*>> saveOrUpdate(
    dao: T,
    modifier: Modifier<T>,
    usernameProvider: UsernameProvider = ContextUsernameProvider(),
): T {
    val username = usernameProvider.fetchUsername()

    return modifier.invoke(
        dao.also {
            dao.isNotPersisted.whenTrue {
                dao.createdBy(creator = username)
            } ?: dao.modifiedBy(modifier = username)
        },
    )
}

@Component
class PersistenceHandlerBean(
    private val usernameProvider: UsernameProvider = ContextUsernameProvider(),
) : PersistenceHandler {
    override suspend fun <T : PersistentDao<*>> save(dao: T, modifier: Modifier<T>): T = suspendTransaction {
        saveOrUpdate(dao, modifier, usernameProvider)
    }

    override suspend fun <T> read(fetch: suspend () -> T): T = suspendTransaction(readOnly = true) { fetch.invoke() }
}
