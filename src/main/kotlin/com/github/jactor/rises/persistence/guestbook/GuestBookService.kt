package com.github.jactor.rises.persistence.guestbook

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

interface GuestBookService {
    suspend fun create(createGuestBook: CreateGuestBook): GuestBook
    suspend fun create(createGuestBookEntry: CreateGuestBookEntry): GuestBookEntry
    suspend fun findGuestBook(id: UUID): GuestBook?
    suspend fun findEntry(id: UUID): GuestBookEntry?
    suspend fun saveOrUpdate(guestBook: GuestBook): GuestBook
    suspend fun saveOrUpdate(guestBookEntry: GuestBookEntry): GuestBookEntry
}

@Service
class GuestBookServiceBean(private val guestBookRepository: GuestBookRepository) : GuestBookService {
    override suspend fun create(createGuestBook: CreateGuestBook): GuestBook = GuestBookDao(
        id = null,
        createdBy = "anonymous",
        modifiedBy = "anonymous",
        timeOfCreation = LocalDateTime.now(),
        timeOfModification = LocalDateTime.now(),
        title = createGuestBook.title,
        userId = createGuestBook.userId,
    ).let { guestBookRepository.save(guestBookDao = it) }.toGuestBook()

    override suspend fun create(
        createGuestBookEntry: CreateGuestBookEntry,
    ): GuestBookEntry = GuestBookEntryDao(
        id = null,
        createdBy = "anonymous",
        modifiedBy = "anonymous",
        timeOfCreation = LocalDateTime.now(),
        timeOfModification = LocalDateTime.now(),
        guestName = createGuestBookEntry.creatorName,
        entry = createGuestBookEntry.entry,
        guestBookId = createGuestBookEntry.guestBookId,
    ).let { guestBookRepository.save(guestBookEntryDao = it) }.toGuestBookEntry()

    override suspend fun findGuestBook(id: UUID): GuestBook? = guestBookRepository.findGuestBookById(id)?.toGuestBook()
    override suspend fun findEntry(id: UUID): GuestBookEntry? = guestBookRepository.findGuestBookEntryById(id)
        ?.toGuestBookEntry()

    override suspend fun saveOrUpdate(guestBook: GuestBook): GuestBook = guestBook.toGuestBookDao().let {
        guestBookRepository.save(guestBookDao = it)
    }.toGuestBook()

    override suspend fun saveOrUpdate(
        guestBookEntry: GuestBookEntry,
    ): GuestBookEntry = guestBookEntry.toGuestBookEntryDao().let { guestBookRepository.save(guestBookEntryDao = it) }
        .toGuestBookEntry()
}
