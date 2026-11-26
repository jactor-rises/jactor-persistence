package com.github.jactor.rises.persistence

import com.github.jactor.rises.shared.whenTrue
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.springframework.stereotype.Component

typealias Persist<T> = suspend (T) -> T

interface PersistenceHandler {
    suspend fun <T> read(fetch: suspend () -> T): T = fetch.invoke()
    suspend fun <T : PersistentDao<*>> save(dao: T, persist: Persist<T>): T = persist.saveOrUpdate(
        dao,
        usernameProvider = UsernameForPersistence.usernameProvider,
    )
}

object UsernameForPersistence {
    var usernameProvider: UsernameProvider = object : UsernameProvider {
        override suspend fun fetchUsername(): String = "anonymous"
    }
}

suspend fun <T : PersistentDao<*>> Persist<T>.saveOrUpdate(
    dao: T,
    usernameProvider: UsernameProvider = ContextUsernameProvider(),
): T {
    val username = usernameProvider.fetchUsername()

    return invoke(
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
    override suspend fun <T> read(fetch: suspend () -> T): T = suspendTransaction(readOnly = true) { fetch.invoke() }
    override suspend fun <T : PersistentDao<*>> save(dao: T, persist: Persist<T>): T = suspendTransaction {
        persist.saveOrUpdate(dao, usernameProvider)
    }
}
