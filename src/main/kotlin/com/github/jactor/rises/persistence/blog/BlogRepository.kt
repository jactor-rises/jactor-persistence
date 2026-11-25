package com.github.jactor.rises.persistence.blog

import com.github.jactor.rises.persistence.PersistenceHandler
import org.springframework.stereotype.Repository
import java.util.UUID

interface BlogRepository {
    suspend fun findBlogById(id: UUID): BlogDao?
    suspend fun findBlogsByUserId(id: UUID): List<BlogDao>
    suspend fun findBlogEntryById(id: UUID): BlogEntryDao?
    suspend fun findBlogsByTitle(title: String): List<BlogDao>
    suspend fun findBlogEntriesByBlogId(id: UUID): List<BlogEntryDao>
    suspend fun save(blogDao: BlogDao): BlogDao
    suspend fun save(blogEntryDao: BlogEntryDao): BlogEntryDao
}

@Repository
class BlogRepositoryBean(
    private val persistenceHandler: PersistenceHandler,
) : BlogRepository {
    override suspend fun findBlogById(id: UUID): BlogDao? = persistenceHandler.read {
        BlogRepositoryObject.findBlogById(id)
    }

    override suspend fun findBlogsByUserId(id: UUID): List<BlogDao> = persistenceHandler.read {
        BlogRepositoryObject.findBlogsByUserId(id)
    }

    override suspend fun findBlogEntryById(id: UUID): BlogEntryDao? = persistenceHandler.read {
        BlogRepositoryObject.findBlogEntryById(id)
    }

    override suspend fun findBlogsByTitle(title: String): List<BlogDao> = persistenceHandler.read {
        BlogRepositoryObject.findBlogsByTitle(title)
    }

    override suspend fun findBlogEntriesByBlogId(id: UUID): List<BlogEntryDao> = persistenceHandler.read {
        BlogRepositoryObject.findBlogEntriesByBlogId(id)
    }

    override suspend fun save(blogDao: BlogDao): BlogDao = persistenceHandler.save(dao = blogDao) {
        BlogRepositoryObject.save(it)
    }

    override suspend fun save(blogEntryDao: BlogEntryDao): BlogEntryDao = persistenceHandler.save(dao = blogEntryDao) {
        BlogRepositoryObject.save(it)
    }
}
