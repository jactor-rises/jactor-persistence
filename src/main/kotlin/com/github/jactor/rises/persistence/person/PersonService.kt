package com.github.jactor.rises.persistence.person

import com.github.jactor.rises.persistence.PersistenceHandler
import org.springframework.stereotype.Service

interface PersonService {
    suspend fun create(person: Person): Person
    suspend fun createWhenNotExists(person: Person): Person?
}

@Service
class PersonServiceBean(
    private val personRepository: PersonRepository,
    private val persistenceHandler: PersistenceHandler,
) : PersonService {
    override suspend fun createWhenNotExists(person: Person): Person? = findExisting(person) ?: create(person)

    private suspend fun findExisting(person: Person): Person? = person.id?.let { personId ->
        persistenceHandler.read { personRepository.findById(personId) }
    }?.toPerson()

    override suspend fun create(person: Person): Person = person.toPersonDao().let { dao ->
        persistenceHandler.modifyAndSave(dao = dao) { personRepository.save(personDao = it) }
    }.toPerson()
}
