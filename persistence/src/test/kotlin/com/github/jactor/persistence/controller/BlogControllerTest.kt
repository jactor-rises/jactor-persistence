package com.github.jactor.persistence.controller

import com.github.jactor.persistence.JactorPersistence
import com.github.jactor.persistence.dto.BlogDto
import com.github.jactor.persistence.dto.BlogEntryDto
import com.github.jactor.persistence.service.BlogService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.verify
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
import java.util.Optional

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [JactorPersistence::class], webEnvironment = WebEnvironment.RANDOM_PORT)
internal class BlogControllerTest {
    @LocalServerPort
    private val port = 0

    @Value("\${server.servlet.context-path}")
    private lateinit var contextPath: String

    @MockBean
    private lateinit var blogServiceMock: BlogService

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun `should build full path`() {
        assertThat(buildFullPath("/somewhere")).isEqualTo("http://localhost:$port/jactor-persistence/somewhere")
    }

    @Test
    fun `should find a blog`() {
        whenever(blogServiceMock.find(1L)).thenReturn(Optional.of(BlogDto()))
        val blogResponse = testRestTemplate.getForEntity(buildFullPath("/blog/1"), BlogDto::class.java)

        assertAll(
            { assertThat(blogResponse.statusCode).`as`("status").isEqualTo(HttpStatus.OK) },
            { assertThat(blogResponse).`as`("blog").isNotNull() }
        )
    }

    @Test
    fun `should not find a blog`() {
        whenever(blogServiceMock.find(1L)).thenReturn(Optional.empty())

        val blogResponse = testRestTemplate.getForEntity(buildFullPath("/blog/1"), BlogDto::class.java)

        assertAll(
            { assertThat(blogResponse.statusCode).`as`("status").isEqualTo(HttpStatus.NO_CONTENT) },
            { assertThat(blogResponse.body).`as`("blog").isNull() }
        )
    }

    @Test
    fun `should find a blog entry`() {
        whenever(blogServiceMock.findEntryBy(1L)).thenReturn(Optional.of(BlogEntryDto()))

        val blogEntryResponse = testRestTemplate.getForEntity(
            buildFullPath("/blog/entry/1"),
            BlogEntryDto::class.java
        )

        assertAll(
            { assertThat(blogEntryResponse.statusCode).`as`("status").isEqualTo(HttpStatus.OK) },
            { assertThat(blogEntryResponse.body).`as`("blog entry").isNotNull() }
        )
    }

    @Test
    fun `should not find a blog entry`() {
        whenever(blogServiceMock.findEntryBy(1L)).thenReturn(Optional.empty())

        val blogEntryResponse = testRestTemplate.getForEntity(
            buildFullPath("/blog/entry/1"),
            BlogEntryDto::class.java
        )

        assertAll(
            { assertThat(blogEntryResponse.statusCode).`as`("status").isEqualTo(HttpStatus.NO_CONTENT) },
            { assertThat(blogEntryResponse.body).`as`("blog entry").isNull() }
        )
    }

    @Test
    fun `should not find blogs by title`() {
        whenever(blogServiceMock.findBlogsBy("Anything")).thenReturn(emptyList())

        val blogResponse = testRestTemplate.exchange(buildFullPath("/blog/title/Anything"), HttpMethod.GET, null, typeIsListOfBlogs())

        assertAll(
            { assertThat(blogResponse.statusCode).`as`("status").isEqualTo(HttpStatus.NO_CONTENT) },
            { assertThat(blogResponse.body).`as`("blogs").isNull() }
        )
    }

    @Test
    fun `hould find blogs by title`() {
        whenever(blogServiceMock.findBlogsBy("Anything")).thenReturn(listOf(BlogDto()))

        val blogResponse = testRestTemplate.exchange(buildFullPath("/blog/title/Anything"), HttpMethod.GET, null, typeIsListOfBlogs())

        assertAll(
            { assertThat(blogResponse.statusCode).`as`("status").isEqualTo(HttpStatus.OK) },
            { assertThat(blogResponse.body).`as`("blogs").isNotEmpty() }
        )
    }

    private fun typeIsListOfBlogs(): ParameterizedTypeReference<List<BlogDto>> {
        return object : ParameterizedTypeReference<List<BlogDto>>() {}
    }

    @Test
    fun `should not find blog entries by blog id`() {
        whenever(blogServiceMock.findEntriesForBlog(1L)).thenReturn(emptyList())

        val blogEntriesResponse = testRestTemplate.exchange(buildFullPath("/blog/1/entries"), HttpMethod.GET, null, typeIsListOfBlogEntries())

        assertAll(
            { assertThat(blogEntriesResponse.statusCode).`as`("status").isEqualTo(HttpStatus.NO_CONTENT) },
            { assertThat(blogEntriesResponse.body).`as`("blogs").isNull() }
        )
    }

    @Test
    fun `should find blog entries by blog id`() {
        whenever(blogServiceMock.findEntriesForBlog(1L)).thenReturn(listOf(BlogEntryDto()))

        val blogEntriesResponse = testRestTemplate.exchange(buildFullPath("/blog/1/entries"), HttpMethod.GET, null, typeIsListOfBlogEntries())

        assertAll(
            { assertThat(blogEntriesResponse.statusCode).`as`("status").isEqualTo(HttpStatus.OK) },
            { assertThat(blogEntriesResponse.body).`as`("blogs").isNotEmpty() }
        )
    }

    private fun typeIsListOfBlogEntries(): ParameterizedTypeReference<List<BlogEntryDto>> {
        return object : ParameterizedTypeReference<List<BlogEntryDto>>() {}
    }

    @Test
    fun `should persist changes to existing blog`() {
        val blogDto = BlogDto()
        blogDto.id = 1L

        whenever(blogServiceMock.saveOrUpdate(blogDto)).thenReturn(blogDto)

        val blogResponse = testRestTemplate.exchange(
            buildFullPath("/blog/1"), HttpMethod.PUT, HttpEntity(blogDto),
            BlogDto::class.java
        )

        assertAll(
            { assertThat(blogResponse.statusCode).`as`("status").isEqualTo(HttpStatus.ACCEPTED) },
            { assertThat(blogResponse.body).`as`("updated blog").isNotNull() },
            { verify(blogServiceMock).saveOrUpdate(blogDto) }
        )
    }

    @Test
    fun `should create a blog`() {
        val blogDto = BlogDto()
        val createdDto = BlogDto()
        createdDto.id = 1L

        whenever(blogServiceMock.saveOrUpdate(blogDto)).thenReturn(createdDto)

        val blogResponse = testRestTemplate.exchange(
            buildFullPath("/blog"), HttpMethod.POST, HttpEntity(blogDto),
            BlogDto::class.java
        )

        assertAll(
            { assertThat(blogResponse.statusCode).`as`("status").isEqualTo(HttpStatus.CREATED) },
            { assertThat(blogResponse).`as`("created blog").isNotNull() },
            { assertThat(blogResponse.body?.id).`as`("blog id").isEqualTo(1L) },
            { verify(blogServiceMock).saveOrUpdate(blogDto) }
        )
    }

    @Test
    fun `should persist changes to existing blog entry`() {
        val blogEntryDto = BlogEntryDto()
        blogEntryDto.id = 1L

        whenever(blogServiceMock.saveOrUpdate(blogEntryDto)).thenReturn(blogEntryDto)

        val blogEntryResponse = testRestTemplate.exchange(
            buildFullPath("/blog/entry/1"), HttpMethod.PUT, HttpEntity(blogEntryDto), BlogEntryDto::class.java
        )

        assertAll(
            { assertThat(blogEntryResponse.statusCode).`as`("status").isEqualTo(HttpStatus.ACCEPTED) },
            { assertThat(blogEntryResponse.body).`as`("updated entry").isNotNull() },
            { verify(blogServiceMock).saveOrUpdate(blogEntryDto) }
        )
    }

    @Test
    fun `should create blog entry`() {
        val blogEntryDto = BlogEntryDto()
        val createdDto = BlogEntryDto()
        createdDto.id = 1L

        whenever(blogServiceMock.saveOrUpdate(blogEntryDto)).thenReturn(createdDto)

        val blogEntryResponse = testRestTemplate.exchange(
            buildFullPath("/blog/entry"), HttpMethod.POST, HttpEntity(blogEntryDto), BlogEntryDto::class.java
        )

        assertAll(
            { assertThat(blogEntryResponse.statusCode).`as`("status").isEqualTo(HttpStatus.CREATED) },
            { assertThat(blogEntryResponse.body).`as`("created entry").isNotNull() },
            { assertThat(blogEntryResponse.body?.id).`as`("blog entry id").isEqualTo(1L) },
            { verify(blogServiceMock).saveOrUpdate(blogEntryDto) }
        )
    }

    private fun buildFullPath(url: String): String {
        return "http://localhost:$port$contextPath$url"
    }
}
