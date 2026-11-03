package com.github.jactor.rises.persistence.user

import com.github.jactor.rises.persistence.PersistenceHandler
import com.github.jactor.rises.persistence.util.suspendedTransaction
import org.springframework.stereotype.Service
import java.util.UUID

interface UserService {
    suspend fun find(username: String): User?
    suspend fun find(id: UUID): User?
    suspend fun update(user: User): User
    suspend fun create(createUser: CreateUser): User
    suspend fun findUsernames(userType: UserType): List<String>
    suspend fun isAlreadyPersisted(username: String): Boolean
}

@Service
class UserServiceBean(
    private val userRepository: UserRepository,
    private val persistenceHandler: PersistenceHandler,
) : UserService {
    override suspend fun find(username: String): User? = suspendedTransaction {
        userRepository.findByUsername(username)?.toUser()
    }

    override suspend fun find(id: UUID): User? = suspendedTransaction {
        userRepository.findById(id = id)?.toUser()
    }

    override suspend fun update(user: User): User = suspendedTransaction {
        persistenceHandler.modifyAndSave(dao = user.toUserDao()) { userRepository.save(userDao = it) }.toUser()
    }

    override suspend fun create(createUser: CreateUser): User = suspendedTransaction {
        persistenceHandler.modifyAndSave(dao = createUser.toUserDao()) { userRepository.save(userDao = it) }
            .toUser()
    }

    override suspend fun findUsernames(userType: UserType): List<String> = suspendedTransaction {
        userRepository.findUsernames(userType = listOf(userType))
    }

    override suspend fun isAlreadyPersisted(username: String): Boolean = suspendedTransaction {
        userRepository.contains(username)
    }
}
