package com.github.jactor.rises.persistence.blog

import com.github.jactor.rises.persistence.PersistenceHandler
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
    private val persistenceHandler: PersistenceHandler,
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
    ).let { blogEntryDao ->
        persistenceHandler.modifyAndSave(dao = blogEntryDao) {
            blogRepository.save(blogEntryDao = it)
        }.toBlogEntry()
    }

    override suspend fun find(id: UUID): Blog? = persistenceHandler.read { blogRepository.findBlogById(id)?.toBlog() }

    override suspend fun findEntryBy(blogEntryId: UUID): BlogEntry? = persistenceHandler.read {
        blogRepository.findBlogEntryById(blogEntryId)?.toBlogEntry()
    }

    override suspend fun findBlogsBy(title: String): List<Blog> = persistenceHandler.read {
        blogRepository.findBlogsByTitle(title).map { it.toBlog() }
    }

    override suspend fun findEntriesForBlog(blogId: UUID): List<BlogEntry> = persistenceHandler.read {
        blogRepository.findBlogEntriesByBlogId(blogId).map { it.toBlogEntry() }
    }

    override suspend fun saveOrUpdate(blog: Blog): Blog = persistenceHandler.modifyAndSave(dao = blog.toBlogDao()) {
        blogRepository.save(blogDao = it)
    }.toBlog()

    override suspend fun saveOrUpdate(blogEntry: BlogEntry): BlogEntry = persistenceHandler.modifyAndSave(
        dao = blogEntry.toBlogEntryDao(),
    ) { blogRepository.save(blogEntryDao = it) }.toBlogEntry()

    override suspend fun update(updateBlogTitle: UpdateBlogTitle): Blog = requireNotNull(
        blogRepository.findBlogById(updateBlogTitle.blogId),
    ) { "Cannot find blog to update" }.apply { this.title = updateBlogTitle.title }
        .let { blog -> persistenceHandler.modifyAndSave(dao = blog) { blogRepository.save(blogDao = it) } }.toBlog()
}
