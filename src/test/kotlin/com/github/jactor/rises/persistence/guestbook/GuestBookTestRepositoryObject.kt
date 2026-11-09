package com.github.jactor.rises.persistence.guestbook

import com.github.jactor.rises.persistence.util.toGuestBookDao
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.util.UUID

object GuestBookTestRepositoryObject {
    suspend fun findGuestBookByUserId(id: UUID): GuestBookDao? = suspendTransaction {
        GuestBooks
            .selectAll()
            .andWhere { GuestBooks.userId eq id }
            .singleOrNull()
            ?.toGuestBookDao()
    }
}
