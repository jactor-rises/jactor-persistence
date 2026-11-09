package com.github.jactor.rises.persistence.blog

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
class BlogRepositoryBean : BlogRepository by BlogRepositoryObject
