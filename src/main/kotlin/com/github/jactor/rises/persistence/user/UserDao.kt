package com.github.jactor.rises.persistence.user

import com.github.jactor.rises.persistence.PersistentDao
import java.time.LocalDateTime
import java.util.UUID

data class UserDao(
    override var id: UUID? = null,
    override var createdBy: String,
    override var timeOfCreation: LocalDateTime,
    override var modifiedBy: String,
    override var timeOfModification: LocalDateTime,
    internal var userRole: UserRole = UserRole.MEMBER,
    internal var emailAddress: String? = null,
    internal var personId: UUID? = null,
    internal var username: String = "na",
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
