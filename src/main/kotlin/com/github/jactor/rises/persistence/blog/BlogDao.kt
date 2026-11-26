package com.github.jactor.rises.persistence.blog

import com.github.jactor.rises.persistence.PersistentDao
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class BlogDao(
    override var id: UUID? = null,
    override var createdBy: String,
    override var timeOfCreation: LocalDateTime,
    override var modifiedBy: String,
    override var timeOfModification: LocalDateTime,
    var created: LocalDate = LocalDate.now(),
    var title: String = "",
    internal var userId: UUID?,
) : PersistentDao<BlogDao> {
    override fun copyWithoutId(): BlogDao = copy(id = null)
    fun toBlog(): Blog = Blog(
        created = created,
        persistent = toPersistent(),
        title = title,
        userId = userId,
    )
}
