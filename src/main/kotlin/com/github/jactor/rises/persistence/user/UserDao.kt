package com.github.jactor.rises.persistence.user

import com.github.jactor.rises.persistence.PersistentDao
import java.time.LocalDateTime
import java.util.UUID

data class UserDao(
    override var id: UUID?,
    override var createdBy: String,
    internal var emailAddress: String?,
    override var modifiedBy: String,
    internal var personId: UUID?,
    override var timeOfCreation: LocalDateTime,
    override var timeOfModification: LocalDateTime,
    internal var userRole: UserRole = UserRole.MEMBER,
    internal var username: String,
) : PersistentDao<UserDao> {
    override fun copyWithoutId(): UserDao = copy(id = null, personId = null)
    fun toUser() = User(
        persistent = toPersistent(),
        username = username,
        emailAddress = emailAddress,
        personId = personId,
        userRole = UserRole.entries.firstOrNull { it.name == userRole.name }
            ?: error("Unknown UserType: $userRole"),
    )
}
