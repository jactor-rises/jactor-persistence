package com.github.jactor.rises.persistence.guestbook

import com.github.jactor.rises.persistence.util.toGuestBookDao
import com.github.jactor.rises.persistence.util.toGuestBookEntryDao
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import java.util.UUID

object GuestBookRepositoryObject : GuestBookRepository {
    override suspend fun findAllGuestBooks(): List<GuestBookDao> = suspendTransaction {
        GuestBooks.selectAll().toList().map { it.toGuestBookDao() }
    }

    override suspend fun findByUserId(userId: UUID): GuestBookDao? = suspendTransaction {
        GuestBooks
            .selectAll()
            .andWhere { GuestBooks.userId eq userId }
            .singleOrNull()
            ?.toGuestBookDao()
    }

    override suspend fun findGuestBookById(id: UUID): GuestBookDao? = suspendTransaction {
        GuestBooks
            .selectAll()
            .andWhere { GuestBooks.id eq id }
            .singleOrNull()
            ?.toGuestBookDao()
    }

    override suspend fun findGuestBookEntryById(id: UUID): GuestBookEntryDao? = suspendTransaction {
        GuestBookEntries
            .selectAll()
            .andWhere { GuestBookEntries.id eq id }
            .singleOrNull()
            ?.toGuestBookEntryDao()
    }

    override suspend fun findGuestBookEtriesByGuestBookId(id: UUID): List<GuestBookEntryDao> = suspendTransaction {
        GuestBookEntries
            .selectAll()
            .andWhere { GuestBookEntries.guestBookId eq id }
            .toList()
            .map { it.toGuestBookEntryDao() }
    }

    override suspend fun save(guestBookDao: GuestBookDao): GuestBookDao = suspendTransaction {
        when (guestBookDao.isPersisted) {
            true -> update(guestBookDao)
            false -> insert(guestBookDao)
        }
    }

    private suspend fun update(guestBookDao: GuestBookDao): GuestBookDao = GuestBooks.update(
        where = { GuestBooks.id eq guestBookDao.id },
    ) {
        it[createdBy] = guestBookDao.createdBy
        it[title] = guestBookDao.title
        it[modifiedBy] = guestBookDao.modifiedBy
        it[timeOfCreation] = guestBookDao.timeOfCreation
        it[timeOfModification] = guestBookDao.timeOfModification
        it[userId] = requireNotNull(guestBookDao.userId) { "UserId cannot be null!" }
    }.let { guestBookDao }

    private suspend fun insert(guestBookDao: GuestBookDao): GuestBookDao = GuestBooks.insertAndGetId {
        it[createdBy] = guestBookDao.createdBy
        it[title] = guestBookDao.title
        it[modifiedBy] = guestBookDao.modifiedBy
        it[timeOfCreation] = guestBookDao.timeOfCreation
        it[timeOfModification] = guestBookDao.timeOfModification
        it[userId] = requireNotNull(guestBookDao.userId) { "UserId cannot be null!" }
    }.value.let { guestBookDao.copy(id = it) }

    override suspend fun save(guestBookEntryDao: GuestBookEntryDao): GuestBookEntryDao = suspendTransaction {
        when (guestBookEntryDao.isPersisted) {
            true -> update(guestBookEntryDao)
            false -> insert(guestBookEntryDao)
        }
    }

    private suspend fun update(guestBookEntryDao: GuestBookEntryDao): GuestBookEntryDao = GuestBookEntries.update(
        where = { GuestBookEntries.id eq guestBookEntryDao.id },
    ) {
        it[createdBy] = guestBookEntryDao.createdBy
        it[guestBookId] = requireNotNull(guestBookEntryDao.guestBookId) { "GuestBookId cannot be null!" }
        it[guestName] = guestBookEntryDao.guestName
        it[entry] = guestBookEntryDao.entry
        it[modifiedBy] = guestBookEntryDao.modifiedBy
        it[timeOfCreation] = guestBookEntryDao.timeOfCreation
        it[timeOfModification] = guestBookEntryDao.timeOfModification
    }.let { guestBookEntryDao }

    private suspend fun insert(guestBookEntryDao: GuestBookEntryDao): GuestBookEntryDao = GuestBookEntries.insertAndGetId {
        it[createdBy] = guestBookEntryDao.createdBy
        it[guestBookId] = requireNotNull(guestBookEntryDao.guestBookId) { "GuestBookId cannot be null!" }
        it[guestName] = guestBookEntryDao.guestName
        it[entry] = guestBookEntryDao.entry
        it[modifiedBy] = guestBookEntryDao.modifiedBy
        it[timeOfCreation] = guestBookEntryDao.timeOfCreation
        it[timeOfModification] = guestBookEntryDao.timeOfModification
    }.value.let { guestBookEntryDao.copy(id = it) }
}
