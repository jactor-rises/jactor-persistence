package com.github.jactor.rises.persistence.guestbook

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
class GuestBookRepositoryBean : GuestBookRepository by GuestBookRepositoryObject
