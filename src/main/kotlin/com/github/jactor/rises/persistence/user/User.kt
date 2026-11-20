package com.github.jactor.rises.persistence.user

import com.github.jactor.rises.persistence.Persistent
import com.github.jactor.rises.shared.api.UserDto
import java.util.UUID

@JvmRecord
data class User(
    val persistent: Persistent = Persistent(),
    val emailAddress: String?,
    val personId: UUID?,
    val username: String?,
    val userRole: UserRole,
) {
    val id: UUID
        get() = persistent.id ?: error("User is not persisted!")

    fun toUserDao() = UserDao(
        id = persistent.id,
        createdBy = persistent.createdBy,
        modifiedBy = persistent.modifiedBy,
        timeOfCreation = persistent.timeOfCreation,
        timeOfModification = persistent.timeOfModification,
        emailAddress = emailAddress,
        personId = personId,
        username = username ?: "na",
        userRole = UserRole.entries.firstOrNull { it.name == userRole.name }
            ?: error(message = "Unknown UserType: $userRole"),
    )

    fun toUserDto() = UserDto(
        persistentDto = persistent.toPersistentDto(),
        emailAddress = emailAddress,
        personId = personId,
        username = username,
        userRole = UserRoleDto.valueOf(userRole.name),
    )
}
