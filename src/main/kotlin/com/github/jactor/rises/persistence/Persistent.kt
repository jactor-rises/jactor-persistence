package com.github.jactor.rises.persistence

import com.github.jactor.rises.shared.api.PersistentDto
import java.time.LocalDateTime
import java.util.UUID

@JvmRecord
data class Persistent(
    val id: UUID? = null,
    val createdBy: String = "not applicable",
    val modifiedBy: String = "not applicable",
    val timeOfCreation: LocalDateTime = LocalDateTime.now(),
    val timeOfModification: LocalDateTime = LocalDateTime.now(),
) {
    fun toPersistentDto() = PersistentDto(
        id = id,
        createdBy = createdBy,
        modifiedBy = modifiedBy,
        timeOfCreation = timeOfCreation,
        timeOfModification = timeOfModification,
    )
}
