package com.github.jactor.rises.persistence

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.fail
import com.github.jactor.rises.persistence.test.countSecondsUntilNow
import com.github.jactor.rises.persistence.test.initAddressDao
import com.github.jactor.rises.persistence.test.initBlogDao
import com.github.jactor.rises.persistence.test.initBlogEntryDao
import com.github.jactor.rises.persistence.test.initGuestBookDao
import com.github.jactor.rises.persistence.test.initGuestBookEntryDao
import com.github.jactor.rises.persistence.test.initPersonDao
import com.github.jactor.rises.persistence.test.initUserDao
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

internal class PersistenceHandlerTest {
    private val oneMinuteAgo = LocalDateTime.now().minusMinutes(1)
    private val persistenceHandler = object : PersistenceHandler {}

    @Test
    fun `should modify timestamp on address when persisted`() = runTestWithUsername(username = "jactor") {
        val create = initAddressDao(id = null, timeOfCreation = oneMinuteAgo, timeOfModification = oneMinuteAgo)
        val modify = initAddressDao(
            id = UUID.randomUUID(),
            timeOfModification = oneMinuteAgo,
            timeOfCreation = oneMinuteAgo,
        )

        assertAll {
            persistenceHandler.save(modify) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "mod create").isEqualTo(60)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "mod modify").isEqualTo(0)
                return@save dao
            }

            persistenceHandler.save(create) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "create create").isEqualTo(0)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "create modify").isEqualTo(0)
                return@save dao
            }
        }
    }

    @Test
    fun `should modify timestamp on blog when persisted`() = runTestWithUsername(username = "jactor") {
        val create = initBlogDao(id = null, timeOfCreation = oneMinuteAgo, timeOfModification = oneMinuteAgo)
        val modify = initBlogDao(
            id = UUID.randomUUID(),
            timeOfCreation = oneMinuteAgo,
            timeOfModification = oneMinuteAgo,
        )

        assertAll {
            persistenceHandler.save(modify) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "mod create").isEqualTo(60)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "mod modify").isEqualTo(0)
                return@save dao
            }

            persistenceHandler.save(create) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "create create").isEqualTo(0)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "create modify").isEqualTo(0)
                return@save dao
            }
        }
    }

    @Test
    fun `should modify timestamp on blogEntry when persistend`() = runTestWithUsername(username = "jactor") {
        val create = initBlogEntryDao(id = null, timeOfCreation = oneMinuteAgo, timeOfModification = oneMinuteAgo)
        val modify = initBlogEntryDao(
            id = UUID.randomUUID(),
            timeOfCreation = oneMinuteAgo,
            timeOfModification = oneMinuteAgo,
        )

        assertAll {
            persistenceHandler.save(modify) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "mod create").isEqualTo(60)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "mod modify").isEqualTo(0)
                return@save dao
            }

            persistenceHandler.save(create) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "create create").isEqualTo(0)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "create modify").isEqualTo(0)
                return@save dao
            }
        }
    }

    @Test
    fun `should modify timestamp on guestBook when persistend`() = runTestWithUsername(username = "jactor") {
        val create = initGuestBookDao(id = null, timeOfCreation = oneMinuteAgo, timeOfModification = oneMinuteAgo)
        val modify = initGuestBookDao(
            id = UUID.randomUUID(),
            timeOfCreation = oneMinuteAgo,
            timeOfModification = oneMinuteAgo,
        )

        assertAll {
            persistenceHandler.save(modify) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "mod create").isEqualTo(60)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "mod modify").isEqualTo(0)
                return@save dao
            }

            persistenceHandler.save(create) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "create create").isEqualTo(0)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "create modify").isEqualTo(0)
                return@save dao
            }
        }
    }

    @Test
    fun `should modify timestamp on guestBookEntry when persisted`() = runTestWithUsername(username = "jactor") {
        val create = initGuestBookEntryDao(id = null, timeOfCreation = oneMinuteAgo, timeOfModification = oneMinuteAgo)
        val modify = initGuestBookEntryDao(
            id = UUID.randomUUID(),
            timeOfCreation = oneMinuteAgo,
            timeOfModification = oneMinuteAgo,
        )

        assertAll {
            persistenceHandler.save(modify) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "mod create").isEqualTo(60)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "mod modify").isEqualTo(0)
                return@save dao
            }

            persistenceHandler.save(create) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "create create").isEqualTo(0)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "create modify").isEqualTo(0)
                return@save dao
            }
        }
    }

    @Test
    fun `should modify timestamp on person when persisted`() = runTestWithUsername(username = "jactor") {
        val create = initPersonDao(id = null, timeOfCreation = oneMinuteAgo, timeOfModification = oneMinuteAgo)
        val modify = initPersonDao(
            id = UUID.randomUUID(),
            timeOfCreation = oneMinuteAgo,
            timeOfModification = oneMinuteAgo,
        )

        assertAll {
            persistenceHandler.save(modify) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "mod create").isEqualTo(60)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "mod modify").isEqualTo(0)
                return@save dao
            }

            persistenceHandler.save(create) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "create create").isEqualTo(0)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "create modify").isEqualTo(0)
                return@save dao
            }
        }
    }

    @Test
    fun `should modify timestamp on user when persisted`() = runTestWithUsername(username = "jactor") {
        val create = initUserDao(id = null, timeOfCreation = oneMinuteAgo, timeOfModification = oneMinuteAgo)
        val modify = initUserDao(
            id = UUID.randomUUID(),
            timeOfCreation = oneMinuteAgo,
            timeOfModification = oneMinuteAgo,
        )

        assertAll {
            persistenceHandler.save(modify) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "mod create").isEqualTo(60)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "mod modify").isEqualTo(0)
                return@save dao
            }

            persistenceHandler.save(create) { dao ->
                assertThat(dao.timeOfCreation.countSecondsUntilNow(), "create create").isEqualTo(0)
                assertThat(dao.timeOfModification.countSecondsUntilNow(), "create modify").isEqualTo(0)
                return@save dao
            }
        }
    }

    @Test
    fun `should apply authenticated username to creator and modifier fields`() = runTestWithUsername(
        username = "authenticated-user",
    ) {
        val createdDao = persistenceHandler.save(
            dao = initAddressDao(id = null, createdBy = "original creator", modifiedBy = "original modifier"),
        ) { it }

        val updatedDao = persistenceHandler.save(
            dao = initAddressDao(
                id = UUID.randomUUID(),
                createdBy = "original creator",
                modifiedBy = "original modifier",
            ),
        ) { it }

        assertAll {
            assertThat(actual = createdDao.createdBy, name = "new creator").isEqualTo("authenticated-user")
            assertThat(actual = createdDao.modifiedBy, name = "new modifier").isEqualTo("authenticated-user")
            assertThat(actual = updatedDao.createdBy, name = "existing creator").isEqualTo("original creator")
            assertThat(actual = updatedDao.modifiedBy, name = "existing modifier").isEqualTo("authenticated-user")
        }
    }

    @Test
    suspend fun `should require username when saving without username context`() {
        NotAuthenticatedUser.usernameProvider = { UsernameContext.fetchUsernameOrFail() }

        val result = runCatching {
            persistenceHandler.save(
                dao = initAddressDao(id = null, createdBy = "original creator", modifiedBy = "original modifier"),
            ) { fail("No username should exist in UsernameContext!") }
        }

        assertThat(actual = result).isFailure().hasMessage("No username in current reactor context!")
    }

    private fun runTestWithUsername(
        username: String = "na",
        testBody: suspend () -> Unit,
    ) = runTest {
        NotAuthenticatedUser.usernameProvider = { username }
        testBody.invoke()
    }
}
