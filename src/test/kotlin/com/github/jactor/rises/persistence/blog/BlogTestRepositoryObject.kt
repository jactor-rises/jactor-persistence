package com.github.jactor.rises.persistence.blog

import com.github.jactor.rises.persistence.util.toBlogDao
import com.github.jactor.rises.persistence.util.toBlogEntryDao
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

object BlogTestRepositoryObject {
    suspend fun findBlogEntries(): List<BlogEntryDao> = suspendTransaction {
        BlogEntries
            .selectAll()
            .toList()
            .map { it.toBlogEntryDao() }
    }

    suspend fun findBlogs(): List<BlogDao> = suspendTransaction {
        Blogs
            .selectAll()
            .toList()
            .map { it.toBlogDao() }
    }
}
