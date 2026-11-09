package com.github.jactor.rises.persistence.blog

import com.github.jactor.rises.persistence.util.toBlogDao
import com.github.jactor.rises.persistence.util.toBlogEntryDao
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import java.util.UUID

object BlogRepositoryObject : BlogRepository {
    override suspend fun findBlogById(id: UUID): BlogDao? = suspendTransaction {
        Blogs
            .selectAll()
            .andWhere { Blogs.id eq id }
            .singleOrNull()
            ?.toBlogDao()
    }

    override suspend fun findBlogsByUserId(id: UUID): List<BlogDao> = suspendTransaction {
        Blogs
            .selectAll()
            .andWhere { Blogs.userId eq id }
            .toList()
            .map { it.toBlogDao() }
    }

    override suspend fun findBlogEntryById(id: UUID): BlogEntryDao? = suspendTransaction {
        BlogEntries
            .selectAll()
            .andWhere { BlogEntries.id eq id }
            .singleOrNull()
            ?.toBlogEntryDao()
    }

    override suspend fun findBlogsByTitle(title: String): List<BlogDao> = suspendTransaction {
        Blogs
            .selectAll()
            .andWhere { Blogs.title eq title }
            .toList()
            .map { it.toBlogDao() }
    }

    override suspend fun findBlogEntriesByBlogId(id: UUID): List<BlogEntryDao> = suspendTransaction {
        BlogEntries
            .selectAll()
            .andWhere { BlogEntries.blogId eq id }
            .toList()
            .map { it.toBlogEntryDao() }
    }

    override suspend fun save(blogDao: BlogDao): BlogDao = suspendTransaction {
        when (blogDao.isNotPersisted) {
            true -> insert(blogDao)
            false -> update(blogDao)
        }
    }

    private suspend fun insert(blogDao: BlogDao): BlogDao = Blogs.insertAndGetId {
        it[Blogs.created] = blogDao.created
        it[Blogs.createdBy] = blogDao.createdBy
        it[Blogs.modifiedBy] = blogDao.modifiedBy
        it[Blogs.timeOfCreation] = blogDao.timeOfCreation
        it[Blogs.timeOfModification] = blogDao.timeOfModification
        it[Blogs.title] = blogDao.title
        it[Blogs.userId] = requireNotNull(blogDao.userId) { "A blog must belong to a user" }
    }.value
        .let { blogDao.copy(id = it) }

    private suspend fun update(blogDao: BlogDao): BlogDao = Blogs.update(
        where = { Blogs.id eq blogDao.id },
    ) { update ->
        update[Blogs.modifiedBy] = blogDao.modifiedBy
        update[Blogs.timeOfModification] = blogDao.timeOfModification
        update[Blogs.created] = blogDao.created
        update[Blogs.title] = blogDao.title
        update[Blogs.userId] = requireNotNull(blogDao.userId) { "A blog must belong to a user" }
    }.let { blogDao }

    override suspend fun save(blogEntryDao: BlogEntryDao): BlogEntryDao = suspendTransaction {
        when (blogEntryDao.isNotPersisted) {
            true -> insert(blogEntryDao)
            false -> update(blogEntryDao)
        }
    }

    private suspend fun insert(blogEntryDao: BlogEntryDao): BlogEntryDao = BlogEntries.insertAndGetId { insert ->
        insert[blogId] = requireNotNull(blogEntryDao.blogId) { "A blog entry must belong to a blog" }
        insert[createdBy] = blogEntryDao.createdBy
        insert[creatorName] = blogEntryDao.creatorName
        insert[entry] = blogEntryDao.entry
        insert[modifiedBy] = blogEntryDao.modifiedBy
        insert[timeOfCreation] = blogEntryDao.timeOfCreation
        insert[timeOfModification] = blogEntryDao.timeOfModification
    }.value.let { blogEntryDao.copy(id = it) }

    private suspend fun update(blogEntryDao: BlogEntryDao): BlogEntryDao = BlogEntries.update(
        where = { BlogEntries.id eq blogEntryDao.id },
    ) { update ->
        update[blogId] = requireNotNull(blogEntryDao.blogId) { "A blog entry must belong to a blog" }
        update[createdBy] = blogEntryDao.createdBy
        update[creatorName] = blogEntryDao.creatorName
        update[entry] = blogEntryDao.entry
        update[modifiedBy] = blogEntryDao.modifiedBy
        update[timeOfCreation] = blogEntryDao.timeOfCreation
        update[timeOfModification] = blogEntryDao.timeOfModification
    }.let { blogEntryDao }
}
