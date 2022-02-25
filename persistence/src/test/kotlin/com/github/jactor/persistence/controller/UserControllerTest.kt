package com.github.jactor.persistence.controller

import com.github.jactor.persistence.JactorPersistence
import com.github.jactor.persistence.dto.UserInternalDto
import com.github.jactor.persistence.entity.UserEntity
import com.github.jactor.persistence.repository.UserRepository
import com.github.jactor.shared.dto.AddressDto
import com.github.jactor.shared.dto.CreateUserCommandDto
import com.github.jactor.shared.dto.PersonDto
import com.github.jactor.shared.dto.UserDto
import com.github.jactor.shared.dto.UserType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [JactorPersistence::class], webEnvironment = WebEnvironment.RANDOM_PORT)
internal class UserControllerTest {

    @LocalServerPort
    private val port = 0

    @Value("\${server.servlet.context-path}")
    private lateinit var contextPath: String

    @MockBean
    private lateinit var userRepositoryMock: UserRepository

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun `should build full path`() {
        assertThat(buildFullPath("/somewhere")).isEqualTo("http://localhost:$port/jactor-persistence/somewhere")
    }

    @Test
    fun `should not find a user by username`() {
        whenever(userRepositoryMock.findByUsername("me")).thenReturn(Optional.empty())

        val userRespnse = testRestTemplate.getForEntity(
            buildFullPath("/user/name/me"),
            UserInternalDto::class.java
        )

        assertAll(
            { assertThat(userRespnse.statusCode).`as`("status").isEqualTo(HttpStatus.NO_CONTENT) },
            { assertThat(userRespnse.body).`as`("user").isNull() }
        )
    }

    @Test
    fun `should find a user by username`() {
        whenever(userRepositoryMock.findByUsername("me")).thenReturn(Optional.of(UserEntity(UserInternalDto())))

        val userResponse = testRestTemplate.getForEntity(
            buildFullPath("/user/name/me"),
            UserInternalDto::class.java
        )

        assertAll(
            { assertThat(userResponse.statusCode).`as`("status").isEqualTo(HttpStatus.OK) },
            { assertThat(userResponse.body).`as`("user").isNotNull() }
        )
    }

    @Test
    fun `should not get a user by id`() {
        whenever(userRepositoryMock.findById(1L)).thenReturn(Optional.empty())

        val userRespnse = testRestTemplate.getForEntity(buildFullPath("/user/1"), UserInternalDto::class.java)

        assertAll(
            { assertThat(userRespnse.statusCode).`as`("status").isEqualTo(HttpStatus.NOT_FOUND) },
            { assertThat(userRespnse.body).`as`("user").isNull() }
        )
    }

    @Test
    fun `should find a user by id`() {
        whenever(userRepositoryMock.findById(1L)).thenReturn(Optional.of(UserEntity(UserInternalDto())))

        val userRespnse = testRestTemplate.getForEntity(buildFullPath("/user/1"), UserInternalDto::class.java)

        assertAll(
            { assertThat(userRespnse.statusCode).`as`("status").isEqualTo(HttpStatus.OK) },
            { assertThat(userRespnse.body).`as`("user").isNotNull() }
        )
    }

    @Test
    fun `should modify existing user`() {
        val userInternalDto = UserInternalDto()
        userInternalDto.id = 1L

        whenever(userRepositoryMock.findById(1L)).thenReturn(Optional.of(UserEntity(userInternalDto)))

        val userRespnse = testRestTemplate.exchange(
            buildFullPath("/user/1"), HttpMethod.PUT, HttpEntity(userInternalDto.toUserDto()), UserDto::class.java
        )

        assertAll(
            { assertThat(userRespnse.statusCode).`as`("status").isEqualTo(HttpStatus.ACCEPTED) },
            { assertThat(userRespnse.body).`as`("user").isNotNull() },
            { assertThat(userRespnse.body?.id).`as`("user id").isEqualTo(1L) }
        )
    }

    @Test
    fun `should find all usernames on active users`() {
        val bartDto = UserDto(person = PersonDto(address = AddressDto()), username = "bart", userType = UserType.ACTIVE)
        val lisaDto = UserDto(person = PersonDto(address = AddressDto()), username = "lisa", userType = UserType.ACTIVE)
        val bart = UserEntity(UserInternalDto(bartDto))
        val lisa = UserEntity(UserInternalDto(lisaDto))

        whenever(userRepositoryMock.findByUserTypeIn(listOf(UserEntity.UserType.ACTIVE))).thenReturn(listOf(bart, lisa))

        val userResponse = testRestTemplate.exchange(buildFullPath("/user/usernames"), HttpMethod.GET, null, responsIslistOfStrings())

        assertAll(
            { assertThat(userResponse.statusCode).`as`("status").isEqualTo(HttpStatus.OK) },
            { assertThat(userResponse.body).`as`("usernames").isEqualTo(listOf("bart", "lisa")) }
        )
    }

    @Test
    fun `should accept if user id is valid`() {
        whenever(userRepositoryMock.findById(101L)).thenReturn(Optional.of(UserEntity(UserInternalDto())))

        val userResponse = testRestTemplate.exchange(
            buildFullPath("/user/101"), HttpMethod.PUT, HttpEntity(UserDto()),
            UserDto::class.java
        )

        assertThat(userResponse.statusCode).isEqualTo(HttpStatus.ACCEPTED)
    }

    @Test
    fun `should not accept if user id is invalid`() {
        whenever(userRepositoryMock.findById(any())).thenReturn(Optional.empty())

        val userResponse = testRestTemplate.exchange(
            buildFullPath("/user/101"), HttpMethod.PUT, HttpEntity(UserDto()),
            UserDto::class.java
        )

        assertThat(userResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    private fun buildFullPath(url: String): String {
        return "http://localhost:$port$contextPath$url"
    }

    private fun responsIslistOfStrings(): ParameterizedTypeReference<List<String>> {
        return object : ParameterizedTypeReference<List<String>>() {}
    }

    @Test
    fun `should return BAD_REQUEST when username is occupied`() {
        whenever(userRepositoryMock.findByUsername("turbo")).thenReturn(Optional.of(UserEntity()))

        val createUserCommand = CreateUserCommandDto()
        createUserCommand.username = "turbo"

        val userResponse = testRestTemplate.exchange(
            buildFullPath("/user"), HttpMethod.POST, HttpEntity(createUserCommand),
            UserDto::class.java
        )

        assertThat(userResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}