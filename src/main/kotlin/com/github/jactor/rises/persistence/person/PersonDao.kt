package com.github.jactor.rises.persistence.person

import com.github.jactor.rises.persistence.PersistentDao
import java.time.LocalDateTime
import java.util.UUID

data class PersonDao(
    override var id: UUID?,
    override var createdBy: String,
    override var timeOfCreation: LocalDateTime,
    override var modifiedBy: String,
    override var timeOfModification: LocalDateTime,
    var description: String?,
    var firstName: String?,
    var locale: String?,
    var surname: String,
    var addressId: UUID?,
) : PersistentDao<PersonDao> {
    override fun copyWithoutId(): PersonDao = copy(id = null, addressId = null)
    fun toPerson() = Person(
        persistent = toPersistent(),
        addressId = addressId,
        locale = locale,
        firstName = firstName,
        surname = surname,
        description = description,
    )
}
