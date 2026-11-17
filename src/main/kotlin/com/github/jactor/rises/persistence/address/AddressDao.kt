package com.github.jactor.rises.persistence.address

import com.github.jactor.rises.persistence.PersistentDao
import java.time.LocalDateTime
import java.util.UUID

data class AddressDao(
    override var id: UUID?,
    override var createdBy: String,
    override var timeOfCreation: LocalDateTime,
    override var modifiedBy: String,
    override var timeOfModification: LocalDateTime,
    var addressLine1: String,
    var addressLine2: String?,
    var addressLine3: String?,
    var city: String,
    var country: String?,
    var zipCode: String,
) : PersistentDao<AddressDao> {
    override fun copyWithoutId(): AddressDao = copy(id = null)
    override fun createdBy(creator: String): AddressDao {
        createdBy = creator
        timeOfCreation = LocalDateTime.now()

        return modifiedBy(modifier = creator)
    }

    override fun modifiedBy(modifier: String): AddressDao {
        modifiedBy = modifier
        timeOfModification = LocalDateTime.now()

        return this
    }

    fun toAddress() = Address(
        persistent = toPersistent(),
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        addressLine3 = addressLine3,
        city = city,
        country = country,
        zipCode = zipCode,
    )
}
