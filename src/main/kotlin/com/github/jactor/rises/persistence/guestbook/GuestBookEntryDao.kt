package com.github.jactor.rises.persistence.guestbook

import com.github.jactor.rises.persistence.EntryDao
import com.github.jactor.rises.persistence.PersistentDao
import java.time.LocalDateTime
import java.util.UUID

data class GuestBookEntryDao(
    override var id: UUID?,
    override var createdBy: String,
    override var modifiedBy: String,
    override var timeOfCreation: LocalDateTime,
    override var timeOfModification: LocalDateTime,
    var guestName: String,
    override var entry: String,
    var guestBookId: UUID?,
) : PersistentDao<GuestBookEntryDao>, EntryDao {
    override var creatorName: String
        get() = guestName
        set(value) {
            guestName = value
        }

    override fun copyWithoutId() = copy(id = null, guestBookId = null)
    fun toGuestBookEntry() = GuestBookEntry(
        persistent = toPersistent(),
        guestName = guestName,
        entry = entry,
        guestBookId = guestBookId,
    )
}
