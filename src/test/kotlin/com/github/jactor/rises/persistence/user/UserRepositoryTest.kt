package com.github.jactor.rises.persistence.user

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.containsAtLeast
import assertk.assertions.isEqualTo
import com.github.jactor.rises.persistence.Persistent
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import com.github.jactor.rises.persistence.test.initAddress
import com.github.jactor.rises.persistence.test.initPerson
import com.github.jactor.rises.persistence.test.initUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UserRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository,
) : AbstractSpringBootNoDirtyContextTest() {
    @BeforeEach
    suspend fun `should delete all non standard users`() {
        super.deleteAllBlogsGuestBooksAndNonStandardUsers()
    }

    @Test
    suspend fun `should find user with username jactor`() {
        val userDao = userRepository.findByUsername("jactor")

        assertThat(userDao?.emailAddress).isEqualTo("tor.egil.jacobsen@gmail.com")
    }

    @Test
    suspend fun `should write then read a user dao`() {
        val address = save(
            address = initAddress(zipCode = "1001", addressLine1 = "Test Boulevard 1", city = "Testington"),
        )

        val person = save(person = initPerson(address = address, surname = "Solo"))

        val userToPersist = User(
            personId = person.id,
            emailAddress = "smuggle.fast@tantooine.com",
            username = "smuggler",
            userRole = UserRole.MEMBER,
        ).toUserDao()

        userRepository.save(userToPersist)

        val userDao = userRepository.findByUsername("smuggler")

        assertAll {
            assertThat(userDao?.personId).isEqualTo(userToPersist.personId)
            assertThat(userDao?.username).isEqualTo("smuggler")
            assertThat(userDao?.emailAddress).isEqualTo("smuggle.fast@tantooine.com")
            assertThat(userDao?.userRole).isEqualTo(UserRole.MEMBER)
        }
    }

    @Test
    suspend fun `should write then update and read a user dao`() {
        val address = save(
            address = initAddress(zipCode = "1001", addressLine1 = "Test Boulevard 1", city = "Testington"),
        )

        val person = save(person = initPerson(address = address, surname = "AA"))
        val userToPersist = initUser(
            persistent = Persistent(),
            person = person,
            emailAddress = "casuel@tantooine.com",
            username = "causual",
        ).toUserDao()

        userRepository.save(userToPersist)

        val lukewarm = "lukewarm"
        userToPersist.username = lukewarm
        userToPersist.emailAddress = "luke@force.com"

        userRepository.save(userToPersist)

        val userDao = userRepository.findByUsername(lukewarm)

        assertAll {
            assertThat(userDao?.username).isEqualTo(lukewarm)
            assertThat(userDao?.emailAddress).isEqualTo("luke@force.com")
        }
    }

    @Test
    suspend fun `should find members and admins`() {
        val address = save(
            address = initAddress(zipCode = "1001", addressLine1 = "Test Boulevard 1", city = "Testington"),
        )

        val spidyPerson = save(person = initPerson(address = address, surname = "Parker"))
        val userDao = initUser(
            persistent = Persistent(),
            person = spidyPerson,
            emailAddress = null,
            username = "spiderman",
        ).toUserDao()

        userRepository.save(userDao)

        val usernames = userRepository.findUsernames(listOf(UserRole.MEMBER, UserRole.ADMIN))

        assertThat(usernames).containsAtLeast("spiderman", "jactor")
    }
}
