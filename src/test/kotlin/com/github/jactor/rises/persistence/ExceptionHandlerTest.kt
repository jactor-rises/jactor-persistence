package com.github.jactor.rises.persistence

import assertk.all
import assertk.assertThat
import assertk.assertions.isSuccess
import assertk.assertions.isTrue
import assertk.fail
import com.github.jactor.rises.persistence.test.containsSubstring
import com.github.jactor.rises.persistence.user.UserController
import com.github.jactor.rises.persistence.user.UserRepository
import com.github.jactor.rises.persistence.user.UserServiceBean
import com.github.jactor.rises.shared.finnFeiledeLinjer
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.io.InvalidObjectException
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
        val userController = UserController(
            userService = UserServiceBean(
                personService = mockk(relaxed = true),
                userRepository = userRepositoryMockk,
                persistenceHandler = object : PersistenceHandler {},
            ),
        )

        coEvery { userRepositoryMockk.findById(any()) } answers { error("boom!") }

        runCatching { userController.get(id = UUID.randomUUID()) }
            .onSuccess { fail("Kjøring skulle feilet!") }
            .onFailure {
                val kodelinjer = it.finnFeiledeLinjer()

                assertThat(kodelinjer).all {
                    containsSubstring("intern: ExceptionHandlerTest.kt (linje:")
                    containsSubstring("...intern: UserService")
                }
            }
    }

    @Test
    suspend fun `should handle generic exceptions with cause without failing`() {
        val userRepositoryMockk = mockk<UserRepository> {}
        val userController = UserController(
            userService = UserServiceBean(
                personService = mockk(relaxed = true),
                userRepository = userRepositoryMockk,
                persistenceHandler = object : PersistenceHandler {},
            ),
        )

        coEvery { userRepositoryMockk.findById(any()) } answers {
            throw IllegalStateException("no way!", InvalidObjectException("not here!"))
        }

        runCatching { userController.get(id = UUID.randomUUID()) }
            .onSuccess { fail("Kjøring skulle feilet!") }
            .onFailure {
                val result = runCatching { exceptionHandler.handleGenericError(it as RuntimeException) }
                assertThat(result).isSuccess()
            }
    }

    class TestController {
        fun illegalArgumentException() = IllegalArgumentException("feil input!!!")
    }
}
