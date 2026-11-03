package com.github.jactor.rises.persistence.user

import com.github.jactor.rises.persistence.PersistenceHandler
import com.github.jactor.rises.persistence.person.PersonService
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
    private val personService: PersonService,
    private val userRepository: UserRepository,
    private val persistenceHandler: PersistenceHandler,
) : UserService {
    override suspend fun find(username: String): User? = persistenceHandler.read {
        userRepository.findByUsername(username)?.toUser()
    }

    override suspend fun find(id: UUID): User? = persistenceHandler.read {
        userRepository.findById(id = id)?.toUser()
    }

    override suspend fun update(user: User): User = user.toUserDao().let { userDao ->
        persistenceHandler.modifyAndSave(dao = userDao) { userRepository.save(userDao = it) }
    }.toUser()

    override suspend fun create(createUser: CreateUser): User = persistenceHandler.save {
        val personId = createUser.personId ?: personService.create(person = createUser.toPerson()).id

        createUser.toUserDao(personId = personId).let { userDao ->
            persistenceHandler.modifyAndSave(dao = userDao) { userRepository.save(userDao = it) }
        }.toUser()
    }

    override suspend fun findUsernames(userType: UserType): List<String> = persistenceHandler.read {
        userRepository.findUsernames(userType = listOf(userType))
    }

    override suspend fun isAlreadyPersisted(username: String): Boolean = persistenceHandler.read {
        userRepository.contains(username)
    }
}
