package com.github.jactor.rises.persistence

import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.springframework.stereotype.Component

typealias Modifier<T> = suspend (T) -> T

interface PersistenceHandler {
    suspend fun <T> read(fetch: suspend () -> T): T = fetch.invoke()
    suspend fun <T : PersistentDao<*>> save(dao: T, modifier: Modifier<T>): T = saveOrUpdate(dao, modifier)
}

private suspend fun <T : PersistentDao<*>> saveOrUpdate(
    dao: T,
    modifier: Modifier<T>,
    usernameProvider: UsernameProvider = ContextUsernameProvider(),
): T {
    val username = usernameProvider.fetchUsername()

    if (dao.isNotPersisted) {
        dao.createdBy(creator = username)
    } else {
        dao.modifiedBy(modifier = username)
    }

    return modifier.invoke(dao)
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
