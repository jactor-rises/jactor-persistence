package com.github.jactor.rises.persistence.guestbook

import com.github.jactor.rises.persistence.EntryDao
import com.github.jactor.rises.persistence.PersistentDao
import java.time.LocalDateTime
import java.util.UUID

data class GuestBookEntryDao(
    override var id: UUID? = null,
    override var createdBy: String = "todo",
    override var modifiedBy: String = "todo",
    override var timeOfCreation: LocalDateTime = LocalDateTime.now(),
    override var timeOfModification: LocalDateTime = LocalDateTime.now(),
    var guestName: String,
    override var entry: String,
    var guestBookId: UUID? = null,
) : PersistentDao<GuestBookEntryDao>,
    EntryDao {
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
