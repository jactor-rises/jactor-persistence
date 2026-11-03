package com.github.jactor.rises.persistence.guestbook

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.github.jactor.rises.persistence.PersistenceHandler
import com.github.jactor.rises.persistence.Persistent
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import com.github.jactor.rises.persistence.test.initGuestBook
import com.github.jactor.rises.persistence.test.initGuestBookEntry
import com.github.jactor.rises.persistence.test.initUser
import com.github.jactor.rises.persistence.test.withId
import com.github.jactor.rises.persistence.user.UserRepository
import com.github.jactor.rises.persistence.user.UserRepositoryObject
import com.github.jactor.rises.persistence.util.toCreateGuestBook
import com.github.jactor.rises.shared.api.CreateGuestBookCommand
import com.github.jactor.rises.shared.api.CreateGuestBookEntryCommand
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

internal class GuestBookServiceTest {
    private val guestBookRepositoryMockk: GuestBookRepository = mockk {}
    private val userRepositoryMockk: UserRepository = mockk {}
    private val guestBookServiceToTest: GuestBookService = GuestBookServiceBean(
        guestBookRepository = guestBookRepositoryMockk,
        persistenceHandler = PersistenceHandler(),
    )

    private val uuid = UUID.randomUUID()

    @Test
    fun `should map guest book to a dto`() = runTest {
        val guestBookDao = initGuestBook(title = "@home").withId().toGuestBookDao().apply {
            userId = UUID.randomUUID()
        }

        every { guestBookRepositoryMockk.findGuestBookById(id = uuid) } returns guestBookDao
        every { userRepositoryMockk.findById(any()) } returns initUser().withId().toUserDao()

        val (_, title, _) = guestBookServiceToTest.findGuestBook(id = uuid) ?: fail { "missed mocking?" }

        assertThat(title).isEqualTo("@home")
    }

    @Test
    fun `should map guest book entry to a dto`() = runTest {
        val anEntry = initGuestBookEntry(
            guestBook = initGuestBook(title = "guest who?").withId(),
            creatorName = "me",
            entry = "too",
            persistent = Persistent(id = uuid),
        ).toGuestBookEntryDao()

        every { guestBookRepositoryMockk.findGuestBookById(id = any()) } returns mockk(relaxed = true)
        every { guestBookRepositoryMockk.findGuestBookEntryById(id = uuid) } returns anEntry

        val guestBookEntryModel = guestBookServiceToTest.findEntry(id = uuid) ?: fail { "missed mocking?" }

        assertAll {
            assertThat(guestBookEntryModel.guestName).isEqualTo("me")
            assertThat(guestBookEntryModel.entry).isEqualTo("too")
        }
    }

    @Test
    fun `should save GuestBookDto as GuestBookDao`() = runTest {
        val guestBookDaoSlot = slot<GuestBookDao>()
        val user = initUser().withId()
        val guestBook = initGuestBook(
            persistent = Persistent(id = uuid),
            title = "home sweet home",
            user = user,
        )

        every { userRepositoryMockk.findById(id = guestBook.userId!!) } returns user.toUserDao()
        every {
            guestBookRepositoryMockk.save(guestBookDao = capture(guestBookDaoSlot))
        } returns guestBook.toGuestBookDao()

        guestBookServiceToTest.saveOrUpdate(guestBook)
        val guestBookDao = guestBookDaoSlot.captured

        assertThat(guestBookDao.title).isEqualTo("home sweet home")
    }

    @Test
    fun `should save GuestBookEntryDto as guestBookEntryDao`() = runTest {
        val guestBookEntryDaoSlot = slot<GuestBookEntryDao>()
        val user = initUser().withId()
        val guestBook = initGuestBook(title = "guest who?", user = user).withId()
        val guestBookEntry = initGuestBookEntry(
            guestBook = guestBook,
            creatorName = "me",
            entry = "if i where a rich man...",
        )

        every { userRepositoryMockk.findById(id = guestBook.userId!!) } returns user.toUserDao()
        every { guestBookRepositoryMockk.findGuestBookById(id = guestBookEntry.guestBookId!!) } returns
            guestBook
                .toGuestBookDao()

        every {
            guestBookRepositoryMockk.save(guestBookEntryDao = capture(guestBookEntryDaoSlot))
        } returns guestBookEntry.toGuestBookEntryDao()

        guestBookServiceToTest.saveOrUpdate(guestBookEntry)
        val guestBookEntryDao = guestBookEntryDaoSlot.captured

        assertAll {
            assertThat(guestBookEntryDao.guestBookId).isNotNull()
            assertThat(guestBookEntryDao.guestName).isEqualTo("me")
            assertThat(guestBookEntryDao.entry).isEqualTo("if i where a rich man...")
        }
    }
}

class GuestBookServiceDbIntegrationTest @Autowired constructor(
    private val guestBookService: GuestBookService,
) : AbstractSpringBootNoDirtyContextTest() {
    @Test
    suspend fun `should create a GuestBook`() {
        val jactorId = UserRepositoryObject.findByUsername(username = "jactor")?.id ?: fail { "Could not find user!" }

        val guestBookId = guestBookService.create(
            createGuestBook = CreateGuestBookCommand(title = "home sweet home", userId = jactorId).toCreateGuestBook(),
        ).id

        val guestBook = guestBookService.findGuestBook(id = guestBookId) ?: fail {
            "Unable to find guest book with id $guestBookId"
        }

        assertThat(guestBook.title).isEqualTo("home sweet home")
    }

    @Test
    suspend fun `should create a GuestBookEntry`() {
        val guestBookId = createGuestBook()

        val guestBookEntryId = guestBookService.create(
            createGuestBookEntry = CreateGuestBookEntryCommand(
                guestBookId = guestBookId,
                creatorName = "sus",
                entry = "shitty, but true",
            ).toCreateGuestBook(),
        ).id ?: fail { "Unable to create guest book entry!" }

        val entry = guestBookService.findEntry(id = guestBookEntryId) ?: fail {
            "Unable to find guest book entry with id $guestBookEntryId"
        }

        assertAll {
            assertThat(entry.guestName).isEqualTo("sus")
            assertThat(entry.entry).isEqualTo("shitty, but true")
        }
    }

    private suspend fun createGuestBook(): UUID {
        val jactorId = UserRepositoryObject.findByUsername(username = "jactor")?.id

        return guestBookService.create(
            createGuestBook = CreateGuestBookCommand(title = "home sweet home", userId = jactorId).toCreateGuestBook(),
        ).id
    }
}
