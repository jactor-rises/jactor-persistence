package com.github.jactor.rises.persistence.guestbook

import com.github.jactor.rises.persistence.PersistentDao
import java.time.LocalDateTime
import java.util.UUID

data class GuestBookDao(
    override var id: UUID?,
    override var createdBy: String,
    override var timeOfCreation: LocalDateTime,
    override var modifiedBy: String,
    override var timeOfModification: LocalDateTime,
    var title: String,
    internal var userId: UUID?,
) : PersistentDao<GuestBookDao?> {
    override fun copyWithoutId(): GuestBookDao = copy(id = null)
    fun toGuestBook(): GuestBook = GuestBook(
        persistent = toPersistent(),
        title = title,
        userId = userId,
    )
}
