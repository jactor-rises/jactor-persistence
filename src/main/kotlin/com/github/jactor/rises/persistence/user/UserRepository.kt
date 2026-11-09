package com.github.jactor.rises.persistence.user

import org.springframework.stereotype.Repository
import java.util.UUID

interface UserRepository {
    suspend fun contains(username: String): Boolean
    suspend fun findById(id: UUID): UserDao?
    suspend fun findByPersonId(id: UUID): List<UserDao>
    suspend fun findByUsername(username: String): UserDao?
    suspend fun findUsernames(userType: List<UserType>): List<String>
    suspend fun save(userDao: UserDao): UserDao
}

@Repository
class UserRepositoryBean : UserRepository by UserRepositoryObject
