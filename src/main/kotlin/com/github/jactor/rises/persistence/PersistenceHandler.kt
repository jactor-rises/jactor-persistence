package com.github.jactor.rises.persistence

import com.github.jactor.rises.shared.whenTrue
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.springframework.stereotype.Component

interface PersistenceHandler {
    suspend fun <T : PersistentDao<*>> modifyAndSave(
        dao: T,
        modifier: suspend (T) -> T,
    ): T {
        modify(dao = dao)
        return modifier.invoke(dao)
    }

    suspend fun <T : PersistentDao<*>> modify(dao: T) = dao.isPersisted.whenTrue { dao.modifiedBy(modifier = "todo") }
    suspend fun <T> read(fetch: suspend () -> T): T = fetch.invoke()
    suspend fun <T> save(saveOrUpdate: suspend () -> T): T = saveOrUpdate.invoke()
}

@Component
class PersistenceHandlerBean : PersistenceHandler {
    override suspend fun <T : PersistentDao<*>> modifyAndSave(
        dao: T,
        modifier: suspend (T) -> T,
    ): T = save {
        modify(dao = dao)
        suspendTransaction { modifier(dao) }
    }

    override suspend fun <T> read(fetch: suspend () -> T): T = suspendTransaction(readOnly = true) { fetch.invoke() }
    override suspend fun <T> save(saveOrUpdate: suspend () -> T): T = suspendTransaction { saveOrUpdate.invoke() }
}
