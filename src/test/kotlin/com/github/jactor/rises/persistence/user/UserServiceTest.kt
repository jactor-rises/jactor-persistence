package com.github.jactor.rises.persistence.user

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.containsAtLeast
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.github.jactor.rises.persistence.PersistenceHandlerBean
import com.github.jactor.rises.persistence.Persistent
import com.github.jactor.rises.persistence.UUIDv7
import com.github.jactor.rises.persistence.person.PersonRepository
import com.github.jactor.rises.persistence.person.PersonRepositoryObject
import com.github.jactor.rises.persistence.person.PersonService
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import com.github.jactor.rises.persistence.test.initAddress
import com.github.jactor.rises.persistence.test.initCreateUserCommand
import com.github.jactor.rises.persistence.test.initPerson
import com.github.jactor.rises.persistence.test.initUser
import com.github.jactor.rises.persistence.test.initUserDao
import com.github.jactor.rises.persistence.util.toCreateUser
import com.github.jactor.rises.shared.api.CreateUserCommand
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

internal class UserServiceTest {
    private val personServiceMockk: PersonService = mockk {}
    private val personRepositoryMockk: PersonRepository = mockk {}
    private val userRepositoryMockk: UserRepository = mockk {}
    private val userServiceToTest = UserServiceBean(
        userRepository = userRepositoryMockk,
        persistenceHandler = PersistenceHandlerBean(),
    )

    @Test
    fun `should map a user dao to a dto`() = runTest {
        val addressDto = initAddress()
        val personDto = initPerson(address = addressDto)

        every { userRepositoryMockk.findByUsername("jactor") } returns initUser(
            person = personDto,
            username = "jactor",
        ).toUserDao()

        val user = userServiceToTest.find("jactor") ?: throw AssertionError("mocking?")

        assertAll {
            assertThat(user).isNotNull()
            assertThat(user.username).isEqualTo("jactor")
        }
    }

    @Test
    fun `should also map a user dao to a dto when finding by id`() = runTest {
        val uuid = UUID.randomUUID()
        val addressDto = initAddress()
        val personDto = initPerson(address = addressDto)

        every { userRepositoryMockk.findById(uuid) } returns initUser(
            person = personDto,
            username = "jactor",
        ).toUserDao()

        val user = userServiceToTest.find(uuid) ?: fail { "null. mocking?" }

        assertAll {
            assertThat(user).isNotNull()
            assertThat(user.username).isEqualTo("jactor")
        }
    }

    @Test
    fun `should update a UserDto with an UserEntity`() = runTest {
        val uuid = UUID.randomUUID()
        val user = initUser(persistent = Persistent(id = uuid), username = "marley")

        every { userRepositoryMockk.findById(id = uuid) } returns user.toUserDao()
        every { userRepositoryMockk.save(any()) } returns user.toUserDao()

        val updatedUser = userServiceToTest.update(user)

        assertThat(updatedUser.username).isEqualTo("marley")
    }

    @Test
    fun `should create and save person for the user`() = runTest {
        val createUserCommand = initCreateUserCommand(
            personId = UUID.randomUUID(),
            username = "jactor",
        )

        every { userRepositoryMockk.save(userDao = any()) } returns initUserDao(createUserCommand = createUserCommand)
        every { personRepositoryMockk.findById(id = createUserCommand.personId!!) } returns mockk {
            every { toPerson() } returns initPerson(surname = "Jacobsen")
        }

        val user = userServiceToTest.create(createUser = createUserCommand.toCreateUser())

        assertThat(user.username).isEqualTo("jactor")
    }
}

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
        assertThat(tip.userType).isEqualTo(UserType.ACTIVE)

        val updated = userService.update(user = tip.copy(userType = UserType.INACTIVE).toUser())
        assertThat(updated.userType).isEqualTo(UserType.INACTIVE)
    }

    @Test
    suspend fun `should find all usernames of active users`() {
        val jactor = UserRepositoryObject.findByUsername(username = "jactor") ?: fail { "User was not found" }
        userService.update(user = jactor.copy(userType = UserType.ACTIVE).toUser())
        val tip = UserRepositoryObject.findByUsername(username = "tip") ?: fail { "User was not found" }
        userService.update(user = tip.copy(userType = UserType.ACTIVE).toUser())

        val activeUsers = userService.findUsernames(userType = UserType.ACTIVE)

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
