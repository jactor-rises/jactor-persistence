package com.github.jactor.rises.persistence.guestbook

import com.github.jactor.rises.persistence.PersistenceHandler
import org.springframework.stereotype.Repository
import java.util.UUID

interface GuestBookRepository {
    suspend fun findAllGuestBooks(): List<GuestBookDao>
    suspend fun findByUserId(userId: UUID): GuestBookDao?
    suspend fun findGuestBookById(id: UUID): GuestBookDao?
    suspend fun findGuestBookEntryById(id: UUID): GuestBookEntryDao?
    suspend fun findGuestBookEtriesByGuestBookId(id: UUID): List<GuestBookEntryDao>
    suspend fun save(guestBookDao: GuestBookDao): GuestBookDao
    suspend fun save(guestBookEntryDao: GuestBookEntryDao): GuestBookEntryDao
}

@Repository
class GuestBookRepositoryBean(private val persistenceHandler: PersistenceHandler) : GuestBookRepository {
    override suspend fun findAllGuestBooks(): List<GuestBookDao> = persistenceHandler.read {
        GuestBookRepositoryObject.findAllGuestBooks()
    }

    override suspend fun findByUserId(userId: UUID): GuestBookDao? = persistenceHandler.read {
        GuestBookRepositoryObject.findByUserId(userId)
    }

    override suspend fun findGuestBookById(id: UUID): GuestBookDao? = persistenceHandler.read {
        GuestBookRepositoryObject.findGuestBookById(id)
    }

    override suspend fun findGuestBookEntryById(id: UUID): GuestBookEntryDao? = persistenceHandler.read {
        GuestBookRepositoryObject.findGuestBookEntryById(id)
    }

    override suspend fun findGuestBookEtriesByGuestBookId(id: UUID): List<GuestBookEntryDao> = persistenceHandler.read {
        GuestBookRepositoryObject.findGuestBookEtriesByGuestBookId(id)
    }

    override suspend fun save(guestBookDao: GuestBookDao): GuestBookDao = persistenceHandler.save(dao = guestBookDao) {
        GuestBookRepositoryObject.save(guestBookDao = it)
    }

    override suspend fun save(guestBookEntryDao: GuestBookEntryDao): GuestBookEntryDao = persistenceHandler.save(
        dao = guestBookEntryDao,
    ) {
        GuestBookRepositoryObject.save(guestBookEntryDao = it)
    }
}
