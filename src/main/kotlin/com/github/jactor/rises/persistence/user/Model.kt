package com.github.jactor.rises.persistence.user

import com.github.jactor.rises.persistence.Persistent
import com.github.jactor.rises.persistence.person.Person
import java.time.LocalDateTime
import java.util.UUID

typealias UserTypeDto = com.github.jactor.rises.shared.api.UserType

@JvmRecord
data class CreateUser(
    val addressId: UUID?,
    val personId: UUID?,
    val username: String,
    val firstName: String?,
    val surname: String,
    val description: String?,
    val emailAddress: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val addressLine3: String?,
    val zipCode: String?,
    val city: String?,
    val language: String?,
    val country: String?,
) {
    fun toUserDao(personId: UUID? = null) = UserDao(
        createdBy = username,
        modifiedBy = username,
        timeOfCreation = LocalDateTime.now(),
        timeOfModification = LocalDateTime.now(),
        emailAddress = emailAddress,
        personId = this.personId ?: personId,
        username = username,
        userType = UserType.ACTIVE,
    )

    fun toPerson() = Person(
        addressId = addressId,
        description = "From ${CreateUser::class.simpleName}: $username",
        firstName = firstName,
        locale = null,
        persistent = Persistent(id = personId),
        surname = surname,
    )
}

enum class UserType {
    ADMIN,
    ACTIVE,
    INACTIVE,
}
