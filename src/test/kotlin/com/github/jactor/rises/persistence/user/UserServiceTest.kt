package com.github.jactor.rises.persistence.user

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.github.jactor.rises.persistence.PersistenceHandler
import com.github.jactor.rises.persistence.Persistent
import com.github.jactor.rises.persistence.UUIDv7
import com.github.jactor.rises.persistence.person.PersonService
import com.github.jactor.rises.persistence.test.initAddress
import com.github.jactor.rises.persistence.test.initCreateUser
import com.github.jactor.rises.persistence.test.initCreateUserCommand
import com.github.jactor.rises.persistence.test.initPerson
import com.github.jactor.rises.persistence.test.initUser
import com.github.jactor.rises.persistence.test.initUserDao
import com.github.jactor.rises.persistence.util.toCreateUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.UUID

internal class UserServiceTest {
    private val personServiceMockk: PersonService = mockk {
        coEvery { create(any()) } answers { firstArg() }
    }

    private val userRepositoryMockk: UserRepository = mockk { coEvery { save(any()) } answers { firstArg() } }
    private val userServiceToTest = UserServiceBean(
        personService = personServiceMockk,
        userRepository = userRepositoryMockk,
        persistenceHandler = object : PersistenceHandler {},
    )

    @Test
    fun `should map a user dao to a dto`() = runTest {
        val addressDto = initAddress()
        val personDto = initPerson(address = addressDto)

        coEvery { userRepositoryMockk.findByUsername("jactor") } returns initUser(
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

        coEvery { userRepositoryMockk.findById(uuid) } returns initUser(
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

        coEvery { userRepositoryMockk.findById(id = uuid) } returns user.toUserDao()
        coEvery { userRepositoryMockk.save(any()) } returns user.toUserDao()

        val updatedUser = userServiceToTest.update(user)

        assertThat(updatedUser.username).isEqualTo("marley")
    }

    @Test
    fun `should create and save person for the user`() = runTest {
        val createUserCommand = initCreateUserCommand(
            personId = UUID.randomUUID(),
            username = "jactor",
        )

        coEvery { userRepositoryMockk.save(userDao = any()) } returns initUserDao(createUserCommand = createUserCommand)

        val user = userServiceToTest.create(createUser = createUserCommand.toCreateUser())

        assertThat(user.username).isEqualTo("jactor")
    }

    @Test
    suspend fun `should create Person for user if personId is not present in CreateUser`() {
        val createUser = initCreateUser(
            personId = null,
        )

        userServiceToTest.create(createUser = createUser)

        coVerify { personServiceMockk.create(any()) }
    }

    @Test
    suspend fun `should not create Person for user if personId is present in CreateUser`() {
        val createUser = initCreateUser(
            personId = UUIDv7.generate(),
        )

        userServiceToTest.create(createUser = createUser)

        coVerify(exactly = 0) { personServiceMockk.createWhenNotExists(any()) }
    }

    @Test
    suspend fun `should use created personId when creating user`() {
        val personId = UUIDv7.generate()
        val createUser = initCreateUser(
            personId = null,
        )

        coEvery { personServiceMockk.create(any()) } returns initPerson(id = personId)

        userServiceToTest.create(createUser = createUser)

        val userSlot = slot<UserDao>()
        coVerify { userRepositoryMockk.save(userDao = capture(userSlot)) }
        assertThat(userSlot.captured.personId).isEqualTo(personId)
    }
}
