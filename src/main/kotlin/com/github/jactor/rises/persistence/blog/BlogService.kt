package com.github.jactor.rises.persistence.blog

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

interface BlogService {
    suspend fun create(createBlogEntry: CreateBlogEntry): BlogEntry
    suspend fun find(id: UUID): Blog?
    suspend fun findBlogsBy(title: String): List<Blog>
    suspend fun findEntriesForBlog(blogId: UUID): List<BlogEntry>
    suspend fun findEntryBy(blogEntryId: UUID): BlogEntry?
    suspend fun saveOrUpdate(blog: Blog): Blog
    suspend fun saveOrUpdate(blogEntry: BlogEntry): BlogEntry
    suspend fun update(updateBlogTitle: UpdateBlogTitle): Blog
}

@Service
class BlogServiceBean(
    private val blogRepository: BlogRepository,
) : BlogService {
    override suspend fun create(createBlogEntry: CreateBlogEntry): BlogEntry = BlogEntryDao(
        id = null,
        createdBy = createBlogEntry.creatorName,
        creatorName = createBlogEntry.creatorName,
        blogId = createBlogEntry.blogId,
        entry = createBlogEntry.entry,
        modifiedBy = createBlogEntry.creatorName,
        timeOfCreation = LocalDateTime.now(),
        timeOfModification = LocalDateTime.now(),
    ).let { blogEntryDao -> blogRepository.save(blogEntryDao = blogEntryDao).toBlogEntry() }

    override suspend fun find(id: UUID): Blog? = blogRepository.findBlogById(id)?.toBlog()
    override suspend fun findEntryBy(blogEntryId: UUID): BlogEntry? = blogRepository.findBlogEntryById(blogEntryId)
        ?.toBlogEntry()

    override suspend fun findBlogsBy(title: String): List<Blog> = blogRepository.findBlogsByTitle(title)
        .map { it.toBlog() }

    override suspend fun findEntriesForBlog(blogId: UUID): List<BlogEntry> = blogRepository.findBlogEntriesByBlogId(
        blogId,
    ).map { it.toBlogEntry() }

    override suspend fun saveOrUpdate(blog: Blog): Blog = blogRepository.save(blogDao = blog.toBlogDao()).toBlog()
    override suspend fun saveOrUpdate(blogEntry: BlogEntry): BlogEntry = blogRepository.save(
        blogEntryDao = blogEntry.toBlogEntryDao(),
    ).toBlogEntry()

    override suspend fun update(updateBlogTitle: UpdateBlogTitle): Blog = requireNotNull(
        blogRepository.findBlogById(updateBlogTitle.blogId),
    ) { "Cannot find blog to update" }.apply { this.title = updateBlogTitle.title }
        .let { blog -> blogRepository.save(blogDao = blog) }.toBlog()
}
