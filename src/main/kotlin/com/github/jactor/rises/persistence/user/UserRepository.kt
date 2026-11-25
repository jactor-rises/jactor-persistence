package com.github.jactor.rises.persistence.user

import com.github.jactor.rises.persistence.PersistenceHandler
import org.springframework.stereotype.Repository
import java.util.UUID

interface UserRepository {
    suspend fun contains(username: String): Boolean
    suspend fun findById(id: UUID): UserDao?
    suspend fun findByPersonId(id: UUID): List<UserDao>
    suspend fun findByUsername(username: String): UserDao?
    suspend fun findUsernames(userRole: List<UserRole>): List<String>
    suspend fun save(userDao: UserDao): UserDao
}

@Repository
class UserRepositoryBean(private val persistenceHandler: PersistenceHandler) : UserRepository {
    override suspend fun contains(username: String): Boolean = persistenceHandler.read {
        UserRepositoryObject.contains(username)
    }

    override suspend fun findById(id: UUID): UserDao? = persistenceHandler.read {
        UserRepositoryObject.findById(id)
    }

    override suspend fun findByPersonId(id: UUID): List<UserDao> = persistenceHandler.read {
        UserRepositoryObject.findByPersonId(id)
    }

    override suspend fun findByUsername(username: String): UserDao? = persistenceHandler.read {
        UserRepositoryObject.findByUsername(username)
    }

    override suspend fun findUsernames(userRole: List<UserRole>): List<String> = persistenceHandler.read {
        UserRepositoryObject.findUsernames(userRole)
    }

    override suspend fun save(userDao: UserDao): UserDao = persistenceHandler.save(dao = userDao) {
        UserRepositoryObject.save(userDao = it)
    }
}
