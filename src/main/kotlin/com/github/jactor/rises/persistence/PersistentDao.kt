package com.github.jactor.rises.persistence

import com.github.jactor.rises.shared.whenTrue
import java.time.LocalDateTime
import java.util.UUID

interface PersistentDao<T> {
    val isPersisted: Boolean get() = id != null
    val isNotPersisted: Boolean get() = !isPersisted

    val id: UUID?
    var createdBy: String
    var modifiedBy: String
    var timeOfCreation: LocalDateTime
    var timeOfModification: LocalDateTime

    fun copyWithoutId(): T
    fun persist(username: String) {
        isNotPersisted.whenTrue {
            createdBy = username
            timeOfCreation = LocalDateTime.now()
        }

        modifiedBy = username
        timeOfModification = LocalDateTime.now()
    }

    fun toPersistent() = Persistent(
        id = id,
        createdBy = createdBy,
        modifiedBy = modifiedBy,
        timeOfCreation = timeOfCreation,
        timeOfModification = timeOfModification,
    )
}
