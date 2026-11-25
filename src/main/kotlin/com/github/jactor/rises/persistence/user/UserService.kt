package com.github.jactor.rises.persistence.user

import com.github.jactor.rises.persistence.person.PersonService
import org.springframework.stereotype.Service
import java.util.UUID

interface UserService {
    suspend fun find(username: String): User?
    suspend fun find(id: UUID): User?
    suspend fun update(user: User): User
    suspend fun create(createUser: CreateUser): User
    suspend fun findUsernames(userRole: UserRole): List<String>
    suspend fun isAlreadyPersisted(username: String): Boolean
}

@Service
class UserServiceBean(
    private val personService: PersonService,
    private val userRepository: UserRepository,
) : UserService {
    override suspend fun find(username: String): User? = userRepository.findByUsername(username)?.toUser()
    override suspend fun find(id: UUID): User? = userRepository.findById(id = id)?.toUser()
    override suspend fun update(user: User): User = user.toUserDao().let { userDao ->
        userRepository.save(userDao = userDao)
    }.toUser()

    override suspend fun create(createUser: CreateUser): User = run {
        val personId = createUser.personId ?: personService.create(person = createUser.toPerson()).id

        createUser.toUserDao(personId = personId).let { userDao -> userRepository.save(userDao = userDao) }.toUser()
    }

    override suspend fun findUsernames(userRole: UserRole): List<String> = userRepository.findUsernames(
        userRole = listOf(userRole),
    )

    override suspend fun isAlreadyPersisted(username: String): Boolean = userRepository.contains(username)
}
