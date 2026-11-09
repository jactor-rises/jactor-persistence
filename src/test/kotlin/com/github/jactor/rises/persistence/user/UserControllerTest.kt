package com.github.jactor.rises.persistence.user

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.fail
import com.github.jactor.rises.persistence.Persistent
import com.github.jactor.rises.persistence.test.all
import com.github.jactor.rises.persistence.test.contains
import com.github.jactor.rises.persistence.test.equals
import com.github.jactor.rises.persistence.test.initUser
import com.github.jactor.rises.persistence.test.named
import com.github.jactor.rises.persistence.test.timestamped
import com.github.jactor.rises.persistence.test.withId
import com.github.jactor.rises.persistence.test.withPersistedData
import com.github.jactor.rises.shared.api.CreateUserCommand
import com.github.jactor.rises.shared.api.PersistentDto
import com.github.jactor.rises.shared.api.UserDto
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.UUID

@WebFluxTest(UserController::class)
internal class UserControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    @MockkBean private val userServiceMockk: UserService,
) {
    @Test
    fun `should not find a user by username`() {
        coEvery { userServiceMockk.find(username = "me") } returns null

        webTestClient
            .get()
            .uri("/user/name/me")
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    fun `should find a user by username`() {
        coEvery { userServiceMockk.find(username = "me") } returns initUser()

        val userDto = webTestClient
            .get()
            .uri("/user/name/me")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<UserDto>()
            .returnResult()
            .responseBody

        assertThat(userDto).isNotNull()
    }

    @Test
    fun `should find a user with ADMIN userType by username`() {
        coEvery { userServiceMockk.find(username = "boss") } returns initUser(userType = UserType.ADMIN)

        val userDto = webTestClient
            .get()
            .uri("/user/name/boss")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<UserDto>()
            .returnResult()
            .responseBody as UserDto

        val userTypeDto = UserTypeDto.valueOf(userDto.userType.name)

        assertThat(userTypeDto).isEqualTo(userDto.userType)
    }

    @Test
    fun `should not get a user by id`() {
        val uuid = UUID.randomUUID()
        coEvery { userServiceMockk.find(id = any()) } returns null

        webTestClient
            .get()
            .uri("/user/$uuid")
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `should find a user by id`() {
        val uuid = UUID.randomUUID()
        coEvery { userServiceMockk.find(id = any()) } returns initUser()

        val userDto = webTestClient
            .get()
            .uri("/user/$uuid")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<UserDto>()
            .returnResult()
            .responseBody

        assertThat(userDto).isNotNull()
    }

    @Test
    fun `should modify existing user`() {
        val uuid = UUID.randomUUID()
        val user = initUser(persistent = Persistent(id = uuid))

        coEvery { userServiceMockk.update(any()) } answers { arg(0) }

        val userDto = webTestClient
            .put()
            .uri("/user/update")
            .bodyValue(user.toUserDto())
            .exchange()
            .expectStatus()
            .isAccepted
            .expectBody<UserDto>()
            .returnResult()
            .responseBody

        assertThat(userDto).isNotNull()
    }

    @Test
    fun `should find all usernames of active users`() {
        coEvery { userServiceMockk.findUsernames(userType = UserType.ACTIVE) } returns listOf("bart", "lisa")

        val usernames = webTestClient
            .get()
            .uri("/user/usernames")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<String>()
            .returnResult()
            .responseBody

        assertThat(usernames).isEqualTo("""["bart","lisa"]""")
    }

    @Test
    fun `should accept if user id is not null`() {
        val uuid = UUID.randomUUID()

        coEvery { userServiceMockk.update(any()) } answers { arg(0) }

        webTestClient
            .put()
            .uri("/user/update")
            .bodyValue(UserDto().withPersistedData(id = uuid))
            .exchange()
            .expectStatus()
            .isAccepted
    }

    @Test
    fun `should not accept if user id is null`() {
        webTestClient
            .put()
            .uri("/user/update")
            .bodyValue(UserDto(persistentDto = PersistentDto(id = null)))
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `should return BAD_REQUEST when username is occupied`() {
        coEvery { userServiceMockk.isAlreadyPersisted(username = "turbo") } returns true

        webTestClient
            .post()
            .uri("/user")
            .bodyValue(CreateUserCommand(username = "turbo"))
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `should create a new user`() {
        val createUserCommand = CreateUserCommand(username = timestamped(username = "turbo"), surname = "Someone")

        coEvery { userServiceMockk.isAlreadyPersisted(any()) } returns false
        coEvery { userServiceMockk.create(any()) } returns
            initUser(
                username = createUserCommand.username,
            )

        webTestClient
            .post()
            .uri("/user")
            .bodyValue(createUserCommand)
            .exchange()
            .expectStatus()
            .isCreated
    }

    @Test
    fun `should get a BAD_REQUEST if trying to create an existing user`() {
        val createUserCommand = CreateUserCommand(username = timestamped(username = "turbo"), surname = "Someone")

        coEvery { userServiceMockk.isAlreadyPersisted(any()) } returns true

        webTestClient
            .post()
            .uri("/user")
            .bodyValue(createUserCommand)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `should create a new user with an email address`() {
        val createUserCommand = CreateUserCommand(
            username = timestamped("turbo"),
            surname = "Someone",
            emailAddress = "somewhere@somehow.com",
        )

        coEvery { userServiceMockk.isAlreadyPersisted(any()) } returns false
        coEvery { userServiceMockk.create(any()) } returns initUser(
            username = createUserCommand.username,
            emailAddress = createUserCommand.emailAddress,
        ).withId()

        val userDto = webTestClient
            .post()
            .uri("/user")
            .bodyValue(createUserCommand)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody<UserDto>()
            .returnResult()
            .responseBody ?: fail(message = "no user created")

        assertThat(userDto).all {
            emailAddress named "email address" equals "somewhere@somehow.com"
            username named "username" contains "turbo"
        }
    }
}
