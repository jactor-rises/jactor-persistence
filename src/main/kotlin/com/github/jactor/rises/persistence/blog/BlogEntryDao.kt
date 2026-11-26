package com.github.jactor.rises.persistence.blog

import com.github.jactor.rises.persistence.EntryDao
import com.github.jactor.rises.persistence.PersistentDao
import java.time.LocalDateTime
import java.util.UUID

data class BlogEntryDao(
    override var id: UUID?,
    override var createdBy: String,
    override var creatorName: String,
    override var entry: String,
    override var modifiedBy: String,
    override var timeOfCreation: LocalDateTime,
    override var timeOfModification: LocalDateTime,
    internal var blogId: UUID,
) : PersistentDao<BlogEntryDao>,
    EntryDao {
    override fun copyWithoutId(): BlogEntryDao = copy(id = null)
    fun toBlogEntry() = BlogEntry(
        persistent = toPersistent(),
        blogId = blogId,
        creatorName = creatorName,
        entry = entry,
    )
}
