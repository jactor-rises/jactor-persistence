package com.github.jactor.rises.persistence.user

import assertk.assertThat
import assertk.assertions.containsAtLeast
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.github.jactor.rises.persistence.UUIDv7
import com.github.jactor.rises.persistence.person.PersonRepositoryObject
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import com.github.jactor.rises.persistence.util.toCreateUser
import com.github.jactor.rises.shared.api.CreateUserCommand
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired

class UserServiceDbIntegrationTest @Autowired constructor(
    private val userService: UserService,
) : AbstractSpringBootNoDirtyContextTest() {
    @Test
    suspend fun `should find a user by username`() {
        assertThat(userService.find(username = "jactor")).isNotNull()
    }

    @Test
    suspend fun `should not find a user by id`() {
        assertThat(userService.find(id = UUIDv7.generate())).isNull()
    }

    @Test
    suspend fun `should find a user by id`() {
        val id = UserRepositoryObject.findByUsername(username = "jactor")?.id ?: fail { "UserId was not found" }
        assertThat(userService.find(id = id)).isNotNull()
    }

    @Test
    suspend fun `should modify an existing user`() {
        val tip = UserRepositoryObject.findByUsername(username = "tip") ?: fail { "User was not found" }
        assertThat(tip.userRole).isEqualTo(UserRole.MEMBER)

        val updated = userService.update(user = tip.copy(userRole = UserRole.ADMIN).toUser())
        assertThat(updated.userRole).isEqualTo(UserRole.ADMIN)
    }

    @Test
    suspend fun `should find all usernames of active users`() {
        val jactor = UserRepositoryObject.findByUsername(username = "jactor") ?: fail { "User was not found" }
        userService.update(user = jactor.copy(userRole = UserRole.MEMBER).toUser())
        val tip = UserRepositoryObject.findByUsername(username = "tip") ?: fail { "User was not found" }
        userService.update(user = tip.copy(userRole = UserRole.MEMBER).toUser())

        val activeUsers = userService.findUsernames(userRole = UserRole.MEMBER)

        assertThat(activeUsers, "active users").containsAtLeast("jactor", "tip")
    }

    @Test
    suspend fun `should create a new user`() {
        val personId = PersonRepositoryObject.findBySurname(surname = "Jacobsen").firstOrNull()?.id
            ?: fail { "PersonId was not found" }

        val createUserCommand = CreateUserCommand(username = "turbo", surname = "Jacobsen", personId = personId)
        val id = userService.create(createUser = createUserCommand.toCreateUser()).id

        assertThat(userService.find(id = id)).isNotNull()
    }

    @Test
    suspend fun `should create a new user with an email address`() {
        val personId = PersonRepositoryObject.findBySurname(surname = "Jacobsen").firstOrNull()?.id
            ?: fail { "PersonId was not found" }

        val createUserCommand = CreateUserCommand(
            username = "turbotape",
            surname = "Jacobsen",
            personId = personId,
            emailAddress = "turbo@jacobsen.com",
        )

        val id = userService.create(createUser = createUserCommand.toCreateUser()).id

        assertThat(userService.find(id = id)?.emailAddress).isEqualTo("turbo@jacobsen.com")
    }
}
