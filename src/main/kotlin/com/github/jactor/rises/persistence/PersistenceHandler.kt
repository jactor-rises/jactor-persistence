package com.github.jactor.rises.persistence

import com.github.jactor.rises.shared.whenTrue
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.springframework.stereotype.Component

typealias Modifier<T> = suspend (T) -> T

interface PersistenceHandler {
    suspend fun <T : PersistentDao<*>> modifyAndSave(dao: T, modifier: Modifier<T>): T = saveOrUpdate(dao, modifier)
    suspend fun <T> read(fetch: suspend () -> T): T = fetch.invoke()
    suspend fun <T> save(saveOrUpdate: suspend () -> T): T = saveOrUpdate.invoke()
}

private suspend fun <T : PersistentDao<*>> saveOrUpdate(dao: T, modifier: Modifier<T>): T {
    dao.isPersisted.whenTrue { dao.modifiedBy(modifier = "todo") }
    return modifier.invoke(dao)
}

@Component
class PersistenceHandlerBean : PersistenceHandler {
    override suspend fun <T : PersistentDao<*>> modifyAndSave(dao: T, modifier: Modifier<T>): T = suspendTransaction {
        saveOrUpdate(dao, modifier)
    }

    override suspend fun <T> read(fetch: suspend () -> T): T = suspendTransaction(readOnly = true) { fetch.invoke() }
    override suspend fun <T> save(saveOrUpdate: suspend () -> T): T = suspendTransaction { saveOrUpdate.invoke() }
}
