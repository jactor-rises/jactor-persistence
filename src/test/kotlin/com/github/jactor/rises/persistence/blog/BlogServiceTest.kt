package com.github.jactor.rises.persistence.blog

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.github.jactor.rises.persistence.Persistent
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import com.github.jactor.rises.persistence.user.UserRepositoryObject
import com.github.jactor.rises.persistence.util.toBlog
import com.github.jactor.rises.shared.api.BlogDto
import com.github.jactor.rises.shared.api.PersistentDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.util.UUID

internal class BlogServiceTest @Autowired constructor(
    private val blogService: BlogService,
) : AbstractSpringBootNoDirtyContextTest() {
    @Test
    suspend fun `should save and read BlogDto`() {
        val blogDtoNotPersisted = BlogDto(
            persistentDto = PersistentDto(createdBy = "test", modifiedBy = "test"),
            title = "full speed ahead",
            userId = UserRepositoryObject.findByUsername(username = "jactor")?.id,
        )

        val blogId = blogService.saveOrUpdate(blog = blogDtoNotPersisted.toBlog()).id
            ?: fail { "BlogId was not returned from saveOrUpdate" }

        val blogDto = blogService.find(id = blogId)?.toBlogDto() ?: fail { "Could not find blog with id $blogId" }

        assertThat(blogDto.title).isEqualTo("full speed ahead")
    }

    @Test
    suspend fun `should create and read BlogEntryDto`() {
        val blogId = createBlog()
        val createBlogEntry = CreateBlogEntry(
            blogId = blogId,
            creatorName = "sus",
            entry = "on the rad again...",
        )

        val blogEntry = blogService.create(createBlogEntry = createBlogEntry)
        val blogEntryDto = blogEntry.toBlogEntryDto()
        val blogEntryId = blogEntry.id ?: fail { "BlogEntryId was not returned from create" }
        val entryById = blogService.findEntryBy(blogEntryId = blogEntryId) ?: fail { "BlogEntry was not found!" }

        assertAll {
            assertThat(blogEntry.creatorName, "creditorName").isEqualTo("sus")
            assertThat(blogEntry.entry, "entry").isEqualTo("on the rad again...")
            assertThat(blogEntryDto.creatorName, "creatorName").isEqualTo("sus")
            assertThat(blogEntryDto.entry, "entry").isEqualTo("on the rad again...")
            assertThat(entryById.entry, "entryById").isEqualTo(blogEntry.entry)
        }
    }

    @Test
    suspend fun `should find entries for blog`() {
        val blogId = createBlog()
        val firstEntry = CreateBlogEntry(
            blogId = blogId,
            creatorName = "sus",
            entry = "on the rad again...",
        )

        blogService.create(createBlogEntry = firstEntry)

        val secondEntry = CreateBlogEntry(
            blogId = blogId,
            creatorName = "sus",
            entry = "flying high...",
        )

        blogService.create(createBlogEntry = secondEntry)

        val entries = blogService.findEntriesForBlog(blogId = blogId)
            .map { it.entry }

        assertAll {
            assertThat(entries, "entries").hasSize(2)
            assertThat(entries, "road...").contains("on the rad again...")
            assertThat(entries, "flying...").contains("flying high...")
        }
    }

    private suspend fun createBlog(): UUID {
        val blogDtoNotPersisted = BlogDto(
            persistentDto = PersistentDto(createdBy = "test", modifiedBy = "test"),
            title = "by JUnit test",
            userId = UserRepositoryObject.findByUsername(username = "jactor")?.id,
        )

        return blogService.saveOrUpdate(blog = blogDtoNotPersisted.toBlog()).id
            ?: fail { "BlogId was not returned from saveOrUpdate" }
    }

    @Test
    suspend fun `should update the blog title`() {
        val createBlog = Blog(
            created = LocalDate.now(),
            persistent = Persistent(createdBy = "test", modifiedBy = "test"),
            title = "full speed ahead",
            userId = UserRepositoryObject.findByUsername(username = "jactor")?.id,
        )

        val blogId = blogService.saveOrUpdate(blog = createBlog).id ?: fail { "BlogId was not returned!" }
        val createdBlog = blogService.find(id = blogId) ?: fail { "Could not find blog with id $blogId" }
        val updateBlogTitle = UpdateBlogTitle(
            blogId = blogId,
            title = "almost full speed ahead",
        )

        blogService.update(updateBlogTitle = updateBlogTitle)

        val blogsByTitle = blogService.findBlogsBy(title = "almost full speed ahead")

        assertAll {
            assertThat(createdBlog.title, "created").isEqualTo("full speed ahead")
            assertThat(blogsByTitle.firstOrNull()?.title, "updated")
                .isEqualTo("almost full speed ahead")
        }
    }
}
