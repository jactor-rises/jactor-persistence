package com.github.jactor.rises.persistence

import assertk.all
import assertk.assertThat
import assertk.assertions.isTrue
import assertk.fail
import com.github.jactor.rises.persistence.test.containsSubstring
import com.github.jactor.rises.persistence.user.UserController
import com.github.jactor.rises.persistence.user.UserRepository
import com.github.jactor.rises.persistence.user.UserServiceBean
import com.github.jactor.rises.shared.finnFeiledeLinjer
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.UUID

internal class ExceptionHandlerTest {
    private val exceptionHandler = ExceptionHandler()

    @Test
    fun `skal være bad request når IllegalArgumentException oppstår`() {
        val response = exceptionHandler.handleBadRequest(e = TestController().illegalArgumentException()).block()

        assertThat(response!!.statusCode.isSameCodeAs(HttpStatus.BAD_REQUEST)).isTrue()
    }

    @Test
    suspend fun `skal hente kodelinjer fra vår kode når exception oppstår`() {
        val userRepositoryMockk = mockk<UserRepository> {}
        val avstemmingController = UserController(
            userService = UserServiceBean(
                personService = mockk(relaxed = true),
                userRepository = userRepositoryMockk,
                persistenceHandler = object : PersistenceHandler {},
            ),
        )

        every { userRepositoryMockk.findById(any()) } answers { error("boom!") }

        runCatching { avstemmingController.get(id = UUID.randomUUID()) }
            .onSuccess { fail("Kjøring skulle feilet!") }
            .onFailure {
                val kodelinjer = it.finnFeiledeLinjer()

                assertThat(kodelinjer).all {
                    containsSubstring("intern: ExceptionHandlerTest.kt (linje:")
                    containsSubstring("...intern: UserService")
                }
            }
    }

    class TestController {
        fun illegalArgumentException() = IllegalArgumentException("feil input!!!")
    }
}
