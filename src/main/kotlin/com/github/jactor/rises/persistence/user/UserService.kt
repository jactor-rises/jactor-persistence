package com.github.jactor.rises.persistence.user

import com.github.jactor.rises.persistence.PersistenceHandler
import com.github.jactor.rises.persistence.util.suspendedTransaction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val persistenceHandler: PersistenceHandler,
) {
    suspend fun find(username: String): User? = suspendedTransaction {
        userRepository.findByUsername(username)?.toUser()
    }

    suspend fun find(id: UUID): User? = suspendedTransaction {
        userRepository.findById(id = id)?.toUser()
    }

    @Transactional
    suspend fun update(user: User): User = suspendedTransaction {
        persistenceHandler.modifyAndSave(dao = user.toUserDao()) {
            userRepository.save(userDao = it)
        }.toUser()
    }

    suspend fun create(createUser: CreateUser): User = suspendedTransaction {
        persistenceHandler.modifyAndSave(
            dao = createUser.toUserDao(),
        ) { userRepository.save(userDao = it) }
            .toUser()
    }

    suspend fun findUsernames(userType: UserType): List<String> = suspendedTransaction {
        userRepository.findUsernames(
            userType = listOf(userType),
        )
    }

    suspend fun isAlreadyPersisted(username: String): Boolean = suspendedTransaction {
        userRepository.contains(username)
    }
}
